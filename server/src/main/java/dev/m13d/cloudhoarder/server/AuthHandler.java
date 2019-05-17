package dev.m13d.cloudhoarder.server;

import dev.m13d.cloudhoarder.common.AuthRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authOk = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (authOk) {
            ctx.fireChannelRead(obj);
            return;
        }
        if (obj instanceof AuthRequest) {
            String userFolder = ""; // user name
            authOk = true;
            ctx.pipeline().addLast(new MainHandler(userFolder));
            ctx.writeAndFlush(new AuthRequest(authOk));
        }
    }
}
