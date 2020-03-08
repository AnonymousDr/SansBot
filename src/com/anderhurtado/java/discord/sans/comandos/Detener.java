package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Detener extends Comando{

    public Detener(){
        super("Obligarme a dejar de cantar para salvar tus oídos","detener","stop","fin");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        Cantante c=Cantante.getCantante(e.getGuild());
        if(c==null){
            e.getChannel().sendMessage("Puedo aceptar que cante mal... ¡Pero esta vez no soy yo!").complete();
            return;
        }c.cancelar();
        e.getChannel().sendMessage("Vale, vale... no me pegues... ya dejo de cantar... no me vuelvas a hablar :(").complete();
    }
}
