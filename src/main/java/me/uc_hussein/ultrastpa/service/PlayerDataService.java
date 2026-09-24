package me.uc_hussein.ultrastpa.service;
import me.uc_hussein.ultrastpa.ULTRASTPAPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
public final class PlayerDataService {
 private final ULTRASTPAPlugin plugin; private final File file; private YamlConfiguration data;
 public PlayerDataService(ULTRASTPAPlugin plugin){ this.plugin=plugin; file=new File(plugin.getDataFolder(),"data.yml"); load(); }
 private void load(){ if(!file.exists()) try{file.createNewFile();}catch(IOException e){plugin.getLogger().warning(e.getMessage());} data=YamlConfiguration.loadConfiguration(file); }
 public void save(){ try{data.save(file);}catch(IOException e){plugin.getLogger().warning(e.getMessage());} }
 private String p(UUID u,String k){return "players."+u+"."+k;}
 public boolean getBool(UUID u,String k,boolean def){return data.getBoolean(p(u,k),def);}
 public void setBool(UUID u,String k,boolean v){data.set(p(u,k),v); save();}
 public Set<String> blocked(UUID u){return new HashSet<>(data.getStringList(p(u,"blocked")));}
 public boolean isBlocked(UUID u,UUID other){return blocked(u).contains(other.toString());}
 public void setBlocked(UUID u,UUID other,boolean value){Set<String>s=blocked(u); if(value)s.add(other.toString());else s.remove(other.toString()); data.set(p(u,"blocked"),new ArrayList<>(s));save();}
 public void ensure(UUID u){String base="players."+u+"."; if(!data.contains(base+"receive-tpa"))data.set(base+"receive-tpa",plugin.getConfig().getBoolean("player-defaults.receive-tpa",true)); if(!data.contains(base+"receive-tpahere"))data.set(base+"receive-tpahere",plugin.getConfig().getBoolean("player-defaults.receive-tpahere",true)); if(!data.contains(base+"receive-all"))data.set(base+"receive-all",plugin.getConfig().getBoolean("player-defaults.receive-tpa-all",true)); if(!data.contains(base+"auto-accept"))data.set(base+"auto-accept",plugin.getConfig().getBoolean("player-defaults.auto-accept",false)); if(!data.contains(base+"open-gui"))data.set(base+"open-gui",plugin.getConfig().getBoolean("player-defaults.open-gui-on-request",true)); save();}
}
