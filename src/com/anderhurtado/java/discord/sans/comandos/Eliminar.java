package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Eliminar extends Comando{

    public Eliminar(){
        super("Eliminar un alias","eliminar","borrar");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<2){
            e.getChannel().sendMessage("Sintaxis: "+args[0]+" <Alias>").complete();
            return;
        }String[] alias=new String[args.length-1];
        for(int x=0;x<alias.length;alias[x]=args[++x]);
        if(Biblioteca.quit(alias))e.getChannel().sendMessage("Las canciones han sido eliminadas de forma exitosa.").complete();
        else e.getChannel().sendMessage("Hubo un error al eliminar las canciones.").complete();
    }
}
