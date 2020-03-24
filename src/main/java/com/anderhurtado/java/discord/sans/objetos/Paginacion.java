package com.anderhurtado.java.discord.sans.objetos;

import com.anderhurtado.java.discord.sans.Inicio;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;

public abstract class Paginacion extends ListenerAdapter{

    final long mensajeID;
    final Message mensaje;
    HashMap<String,Boton> botonesHM=new HashMap<>();

    public Paginacion(Message msg,Boton... botones){
        mensaje=msg;
        mensajeID=msg.getIdLong();
        for(Boton b:botones){
            if(b==null)continue;
            msg.addReaction(b.CODIGO).complete();
            botonesHM.put(b.CODIGO,b);
        }Inicio.discord.addEventListener(this);
    }

    public void detener(){
        Inicio.discord.removeEventListener(this);
        if(mensaje.getGuild().getMember(Inicio.discord.getSelfUser()).hasPermission(Permission.MESSAGE_MANAGE))botonesHM.keySet().forEach(s->mensaje.clearReactions(s).complete());
        else botonesHM.keySet().forEach(s->mensaje.removeReaction(s).complete());
    }

    public abstract void evento(GuildMessageReactionAddEvent e,Boton b);

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e){
        if(e.getMessageIdLong()!=mensajeID||e.getMember().getUser().isBot())return;
        Boton b=botonesHM.get(e.getReactionEmote().getEmoji());
        if(b!=null)evento(e,b);
    }
}
