package me.uc_hussein.ultrastpa.service;
import me.uc_hussein.ultrastpa.ULTRASTPAPlugin; import me.uc_hussein.ultrastpa.gui.TPAGui; import me.uc_hussein.ultrastpa.model.*;
import org.bukkit.*; import org.bukkit.entity.Player; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
public final class RequestService {
 private final ULTRASTPAPlugin plugin; private final Map<UUID,LinkedHashMap<UUID,TeleportRequest>> incoming=new ConcurrentHashMap<>(); private final Map<UUID,LinkedHashMap<UUID,TeleportRequest>> outgoing=new ConcurrentHashMap<>();
 public RequestService(ULTRASTPAPlugin p){plugin=p; Bukkit.getScheduler().runTaskTimer(p,this::expire,20L,20L);}
 public Collection<TeleportRequest> incoming(UUID u){return incoming.getOrDefault(u,new LinkedHashMap<>()).values();}
 public TeleportRequest latest(UUID u){var m=incoming.get(u); if(m==null||m.isEmpty())return null; return m.values().stream().max(Comparator.comparingLong(TeleportRequest::createdAt)).orElse(null);}
 public TeleportRequest outgoingTo(UUID sender,UUID target){var m=outgoing.get(sender); return m==null?null:m.get(target);}
 public void send(Player sender,Player target,RequestType type){
  long now=System.currentTimeMillis(); long exp=now+plugin.seconds("settings.request-expiration-seconds")*1000L;
  UUID id=UUID.randomUUID(); TeleportRequest r=new TeleportRequest(id,sender.getUniqueId(),target.getUniqueId(),type,now,exp);
  incoming.computeIfAbsent(target.getUniqueId(),x->new LinkedHashMap<>()).put(sender.getUniqueId(),r); outgoing.computeIfAbsent(sender.getUniqueId(),x->new LinkedHashMap<>()).put(target.getUniqueId(),r);
  plugin.log("REQUEST",sender.getName()+" -> "+target.getName()+" type="+type); if(plugin.getConfig().getBoolean("settings.sounds-enabled",true)) target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.55f, 1.25f);
  if(plugin.data().getBool(target.getUniqueId(),"auto-accept",false)){accept(target,r);} else if(plugin.data().getBool(target.getUniqueId(),"open-gui",true)&&plugin.getConfig().getBoolean("settings.open-gui-on-request",true)){new TPAGui(plugin).openRequest(target,r);} else plugin.messages().requestButton(target,sender.getName(),plugin.messages().type(type));
 }
 public void accept(Player target,TeleportRequest r){if(!valid(target,r))return; Player sender=Bukkit.getPlayer(r.sender()); if(sender==null){remove(r); return;} remove(r); Player teleporter=r.type()==RequestType.TPAHERE?target:sender; Player destination=r.type()==RequestType.TPAHERE?sender:target; // person who moves and destination
  startTeleport(teleporter,destination,r.type()); plugin.messages().send(target,"request-accepted-target","%player%",sender.getName(),"%type%",plugin.messages().type(r.type())); plugin.messages().send(sender,"request-accepted-sender","%player%",target.getName(),"%type%",plugin.messages().type(r.type())); }
 public void deny(Player target,TeleportRequest r){if(!valid(target,r))return; Player sender=Bukkit.getPlayer(r.sender()); remove(r); plugin.messages().send(target,"request-denied-target","%player%",sender==null?"Unknown":sender.getName(),"%type%",plugin.messages().type(r.type())); if(sender!=null)plugin.messages().send(sender,"request-denied-sender","%player%",target.getName(),"%type%",plugin.messages().type(r.type())); plugin.log("DENY",target.getName()+" denied request id="+r.id());}
 public void cancelSender(Player sender,Player target){TeleportRequest r=outgoingTo(sender.getUniqueId(),target.getUniqueId()); if(r==null){plugin.messages().send(sender,"no-outgoing-to","%player%",target.getName());return;} remove(r); plugin.messages().send(sender,"request-cancelled","%player%",target.getName()); plugin.log("CANCEL",sender.getName()+" cancelled request to "+target.getName());}
 public void cancelLatest(Player sender){var m=outgoing.get(sender.getUniqueId()); if(m==null||m.isEmpty()){plugin.messages().send(sender,"no-outgoing");return;} TeleportRequest r=m.values().stream().max(Comparator.comparingLong(TeleportRequest::createdAt)).orElseThrow(); Player t=Bukkit.getPlayer(r.target()); remove(r); plugin.messages().send(sender,"request-cancelled","%player%",t==null?"Unknown":t.getName());}
 private boolean valid(Player target,TeleportRequest r){if(r.expiresAt()<=System.currentTimeMillis()){remove(r);plugin.messages().send(target,"request-expired-target","%player%",Bukkit.getOfflinePlayer(r.sender()).getName()==null?"Unknown":Bukkit.getOfflinePlayer(r.sender()).getName(),"%type%",plugin.messages().type(r.type()));return false;} if(plugin.isWorldDisabled(target.getWorld())|| (Bukkit.getPlayer(r.sender())!=null&&plugin.isWorldDisabled(Bukkit.getPlayer(r.sender()).getWorld()))){plugin.messages().send(target,"world-disabled");return false;} return true;}
 private void remove(TeleportRequest r){incoming.getOrDefault(r.target(),new LinkedHashMap<>()).remove(r.sender()); outgoing.getOrDefault(r.sender(),new LinkedHashMap<>()).remove(r.target());}
 private void expire(){long now=System.currentTimeMillis(); for(var map:new ArrayList<>(incoming.values()))for(var r:new ArrayList<>(map.values()))if(r.expiresAt()<=now){Player s=Bukkit.getPlayer(r.sender()),t=Bukkit.getPlayer(r.target());remove(r); if(s!=null)plugin.messages().send(s,"request-expired-sender","%player%",t==null?"Unknown":t.getName(),"%type%",plugin.messages().type(r.type())); if(t!=null)plugin.messages().send(t,"request-expired-target","%player%",s==null?"Unknown":s.getName(),"%type%",plugin.messages().type(r.type()));plugin.log("EXPIRE","request="+r.id());}}
 private void startTeleport(Player mover,Player destination,RequestType type){
  if(plugin.isWorldDisabled(mover.getWorld())||plugin.isWorldDisabled(destination.getWorld())){plugin.messages().send(mover,"world-disabled");return;}
  int sec=plugin.seconds("settings.teleport-delay-seconds");
  Location start=mover.getLocation().clone();
  long totalTicks=Math.max(1,sec*20L);
  plugin.messages().send(mover,"teleport-start","%player%",destination.getName(),"%time%",String.valueOf(sec),"%type%",plugin.messages().type(type));
  plugin.messages().send(destination,"teleport-start-target","%player%",mover.getName(),"%time%",String.valueOf(sec),"%type%",plugin.messages().type(type));
  final long[] elapsed={0};
  int period=Math.max(1,plugin.getConfig().getInt("countdown.actionbar-update-ticks",2));
  Bukkit.getScheduler().runTaskTimer(plugin,task->{
   if(!mover.isOnline()||!destination.isOnline()){task.cancel();return;}
   if(plugin.getConfig().getBoolean("settings.cancel-teleport-on-move",true)&&moved(start,mover.getLocation(),plugin.getConfig().getDouble("settings.move-threshold-blocks",0.01))){
    plugin.messages().send(mover,"teleport-cancelled-move"); task.cancel(); return;
   }
   long left=Math.max(0,totalTicks-elapsed[0]);
   if(plugin.getConfig().getBoolean("countdown.enabled",true)){
    double seconds=left/20.0;
    String value=String.format(java.util.Locale.US,"%.1f",seconds);
    plugin.messages().actionbar(mover,"teleport-countdown","%player%",destination.getName(),"%time%",value);
   }
   if(left<=0){
    task.cancel();
    Location loc=destination.getLocation().clone();
    mover.teleportAsync(loc).thenRun(()->{
     plugin.messages().actionbar(mover,"teleported-actionbar","%player%",destination.getName());
     plugin.messages().send(mover,"teleported","%player%",destination.getName());
     if(plugin.getConfig().getBoolean("settings.sounds-enabled",true))mover.playSound(mover.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,0.35f,1.45f);
     if(plugin.getConfig().getBoolean("settings.particles-enabled",true))mover.getWorld().spawnParticle(Particle.END_ROD,mover.getLocation().add(0,1,0),18,0.35,0.5,0.35,0.01);
     plugin.log("TELEPORT",mover.getName()+" -> "+destination.getName());
    });
   }
   elapsed[0]+=period;
  },0L,period);
 }
 private boolean moved(Location a,Location b,double threshold){return !a.getWorld().equals(b.getWorld())||a.distanceSquared(b)>threshold*threshold;}
}
