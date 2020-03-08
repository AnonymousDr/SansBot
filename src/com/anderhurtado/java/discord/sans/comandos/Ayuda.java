package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Comando;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Ayuda extends Comando{

    public Ayuda(){
        super("Visualizar los comandos","ayuda","comandos","help");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        String txt=getAyuda(Comando.COMANDOS[0]);
        for(int x=1;x<Comando.COMANDOS.length;x++)txt+="\n"+getAyuda(Comando.COMANDOS[x]);
        e.getChannel().sendMessage(txt).complete();
    }

    public static String getAyuda(Comando cmd){
        return "/"+cmd.nombres[0]+" -> "+cmd.descripcion;
    }
}
