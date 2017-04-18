package org.iml1s.messagecenter.wrapper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jsontech.inc.MessageCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by ImL1s on 2017/4/18.
 * <p>
 * DESC:
 */

public class OrangeMessageCenter implements IMessageManager {

    private MessageCenter messageCenter;
    private List<MessageCenter.MessageEventListener> listenerList = new ArrayList<>();


    @Override
    public void init(Context appContext) {
        init(appContext, PollingMode.BACKGROUND);
    }

    @Override
    public void init(Context appContext, PollingMode pollingMode) {
        MessageCenter.init(appContext, pollingMode == PollingMode.BACKGROUND ? MessageCenter.PollingMode.BACKGROUND : MessageCenter.PollingMode.POSTING);
        messageCenter = MessageCenter.getInstance();
    }

    @Override
    public void addListener(String messageName, MessageEventListener messageEventListener) {
        messageCenter.addListener(messageName, convertToInnerEventListener(messageEventListener));
    }

    @Override
    public void addListenerSticky(String messageName, MessageEventListener messageEventListener) {
        messageCenter.addListenerSticky(messageName, convertToInnerEventListener(messageEventListener));
    }

    @Override
    public void addListener(String messageName, MessageEventListener messageEventListener, boolean sticky) {
        messageCenter.addListener(messageName, convertToInnerEventListener(messageEventListener), sticky);
    }

    @Override
    public void removeListener(String msgName, MessageEventListener messageEventListener) {
        messageCenter.removeListener(msgName, convertToInnerEventListener(messageEventListener));
        removeExternalListener(messageEventListener);
    }

    @Override
    public void clearStickyEvent() {
        messageCenter.clearStickyEvent();
    }

    @Override
    public void dispatchAllStickyEvent() {
        messageCenter.dispatchAllStickyEvent();
    }

    @Override
    public void sendEmpty(@Nullable String name) {
        messageCenter.sendEmpty(name);
    }

    @Override
    public void sendEmptySticky(@Nullable String name) {
        messageCenter.sendEmptySticky(name);
    }

    @Override
    public void sendEmpty(@Nullable String name, @Nullable Object sender) {
        messageCenter.sendEmpty(name, sender);
    }

    @Override
    public void send(@NonNull String name, @Nullable Object sender, @Nullable Object content, @Nullable Class<?> contentType, @Nullable Map<String, Object> dicPara) {
        messageCenter.send(name, sender, content, contentType, dicPara);
    }

    @Override
    public void send(@NonNull Message message) {
        messageCenter.send(convertToInnerMessage(message));
    }

    @Override
    public void sendSticky(@NonNull String name, @Nullable Object sender, @Nullable Object content, @Nullable Class<?> contentType, @Nullable Map<String, Object> dicPara) {
        messageCenter.sendSticky(name, sender, content, contentType, dicPara);
    }

    @Override
    public void sendSticky(@NonNull Message message) {
        messageCenter.sendSticky(convertToInnerMessage(message));
    }

    private MessageCenter.Message convertToInnerMessage(Message message) {
        MessageCenter.Message innerMessage = new MessageCenter.Message(message.getName(), message.getSender(), message.getContent(), message.getContentType(), message.getDicData());
        return innerMessage;
    }

    private Message convertToExternalMessage(MessageCenter.Message message) {
        Message externalMessage = new Message(message.getName(), message.getSender(), message.getContent(), message.getContentType(), message.getDicData(), this);
        return externalMessage;
    }

    private MessageCenter.MessageEventListener convertToInnerEventListener(final MessageEventListener listener) {
        if (listenerList.contains(listener)) {
            return listenerList.get(listenerList.indexOf(listener));
        }

        MessageCenter.MessageEventListener innerListener;

        if (listener instanceof MainThreadEventListener) {
            innerListener = new MessageCenter.MainThreadEventListener() {
                @Override
                public void onEvent(MessageCenter.Message msg) {
                    listener.onEvent(convertToExternalMessage(msg));
                }
            };
        } else if (listener instanceof NewThreadEventListener) {
            innerListener = new MessageCenter.NewThreadEventListener() {
                @Override
                public void onEvent(MessageCenter.Message msg) {
                    listener.onEvent(convertToExternalMessage(msg));
                }
            };
        } else {
            innerListener = new MessageCenter.MessageEventListener() {

                @Override
                public void onEvent(MessageCenter.Message msg) {
                    listener.onEvent(convertToExternalMessage(msg));
                }
            };
        }
        listenerList.add(innerListener);
        return innerListener;
    }


    private void removeExternalListener(MessageEventListener listener) {
        listenerList.remove(listener);
    }
}
