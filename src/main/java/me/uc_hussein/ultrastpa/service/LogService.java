package me.uc_hussein.ultrastpa.service;
import me.uc_hussein.ultrastpa.ULTRASTPAPlugin;
import java.io.*;import java.nio.charset.StandardCharsets;import java.time.LocalDateTime;import java.time.format.DateTimeFormatter;
public final class LogService { private final ULTRASTPAPlugin plugin; private final DateTimeFormatter fmt=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); public LogService(ULTRASTPAPlugin p){plugin=p;}
 public synchronized void log(String action,String details){if(!plugin.getConfig().getBoolean("logging.enabled",true))return; File f=new File(plugin.getDataFolder(),plugin.getConfig().getString("logging.file-name","actions.log")); try(FileWriter w=new FileWriter(f,true)){w.write("["+LocalDateTime.now().format(fmt)+"] ["+action+"] "+details+System.lineSeparator());}catch(IOException e){plugin.getLogger().warning("Could not write log: "+e.getMessage());}}
}
