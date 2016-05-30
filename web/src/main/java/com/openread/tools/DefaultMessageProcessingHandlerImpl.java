package com.openread.tools;

import com.openread.pojo.InMessage;
import com.openread.pojo.OutMessage;

public abstract class DefaultMessageProcessingHandlerImpl implements MessageProcessingHandler {

    private OutMessage allType(InMessage msg) {
        OutMessage out = new OutMessage();
        out.setContent("你的消息已经收到！");
        return out;
    }

    
    public OutMessage textTypeMsg(InMessage msg) {
        return allType(msg);
    }

    
    public OutMessage locationTypeMsg(InMessage msg) {
        return allType(msg);
    }

    
    public OutMessage imageTypeMsg(InMessage msg) {
        return allType(msg);
    }

    public OutMessage linkTypeMsg(InMessage msg) {
        return allType(msg);
    }

    
    public OutMessage eventTypeMsg(InMessage msg) {
        return allType(msg);
    }

}
