package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Comando;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Perdon extends Comando{

    public Perdon(){
        super("Comando secreto","perdon","perdón","perdonar");
    }

    final String[] FRASES=new String[]{"¡No! ¡No te perdonaré jamás en la vida!","Bueno, mira, esta vez te perdonaré, pero que no vuelva a pasar...","¿Yo? ¿Que motivos tendría para no perdonarte? ¡Perdonado! ¡Todos perdonados! Excepto a ese, que me cae muy mal..."};

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        e.getChannel().sendMessage(FRASES[(int)(Math.random()*FRASES.length)]).complete();
    }
}
