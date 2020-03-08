package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Aleatorio extends Comando{

    public Aleatorio(){
        super("Reestablece el orden de las canciones a cantar","aleatorio","aleatorizar","mezcla","mezclar");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        Cantante c=Cantante.getCantante(e.getGuild());
        if(c==null){
            e.getChannel().sendMessage("¡No estoy cantando nada aquí! ¿Karaoke?").complete();
            return;
        }int n=c.getPlayList().length;
        if(n<=1){
            e.getChannel().sendMessage((n==0?"¡No hay canciones!":"¡Solo hay una canción!")+" ¿Que canciones vas a mezclar?").complete();
            return;
        }c.aleatorizar();
        e.getChannel().sendMessage("¡Canciones mezcladas!").complete();
    }
}
