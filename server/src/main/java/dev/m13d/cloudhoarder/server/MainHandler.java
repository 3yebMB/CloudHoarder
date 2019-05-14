package dev.m13d.cloudhoarder.server;

import dev.m13d.cloudhoarder.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MainHandler extends ChannelInboundHandlerAdapter {
    private String userFolder;
    private String root;

    public MainHandler(String userFolder) {
        this.userFolder = userFolder;
        this.root = new StringBuilder("server").append(File.separator).append("server_storage").append(File.separator)
                                               .append(this.userFolder).append(File.separator).toString();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get(root + fr.getFileName()))) {
                    FileMessage fm = new FileMessage(Paths.get(root + fr.getFileName()));
                    ctx.writeAndFlush(fm);
                }
                return;
            }
            if (msg instanceof FileMessage) {
//                FileMessage fm = (FileMessage) msg;
                Files.write(Paths.get(root + ((FileMessage) msg).getFileName()), ((FileMessage) msg).getData(), StandardOpenOption.CREATE);
//                Files.
//                System.out.println("File has been received");
                sendFileList(ctx);
                return;
            }
            if (msg instanceof FileListRequest) {
                sendFileList(ctx);
                return;
            }
            if (msg instanceof FileRenameRequest) {
                FileRenameRequest renameRequest = (FileRenameRequest) msg;
                File fileOld = new File(root + renameRequest.getFileName());
                File fileNew = new File(root + renameRequest.getNewFileName());
                fileOld.renameTo(fileNew);
                sendFileList(ctx);
                return;
            }
            if (msg instanceof FileDeleteRequest) {
                FileDeleteRequest deleteRequest = (FileDeleteRequest) msg;
                Files.delete(Paths.get(root + deleteRequest.getFileName()));
                sendFileList(ctx);
                return;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void sendFileList(ChannelHandlerContext ctx) throws IOException {
        FileListRequest listRequest = new FileListRequest();
        Files.list(Paths.get(root)).map(p -> p.getFileName().toString()).forEach(o -> listRequest.addFile(o));
        ctx.writeAndFlush(listRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
