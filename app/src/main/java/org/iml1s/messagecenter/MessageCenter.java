package org.iml1s.messagecenter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by ImL1s on 2017/4/12.
 * <p>
 * DESC:
 */

public class MessageCenter {

    public final static int MSG_RUN_CALL_BACK_ON_MAIN_THREAD = 0;

    private final static String TAG = MessageCenter.class.getName();
    private static MessageCenter instance;

    private Map<String, List<MessageEventListener>> dicRegisteredEvents = new HashMap<>();
    private List<Message> stickyEvents = new ArrayList<>();
    private Queue<Message> msgQueue;
    private Context appContext;
    private Handler handler;
    private PollingMode pollingMode;
    private boolean polling;


    public static void init(Context appContext) {
        init(appContext, PollingMode.POSTING);
    }

    public static void init(Context appContext, PollingMode pollingMode) {
        if (instance == null) {
            instance = new MessageCenter(appContext);
            instance.pollingMode = pollingMode;
        } else
            Log.w(TAG, "do not init multiple...");

        if (pollingMode == PollingMode.BACKGROUND) {
            instance.msgQueue = new PriorityQueue<>();
            instance.polling = true;
            instance.startPolling();
        }
    }


    public static MessageCenter getInstance() {
        if (instance == null) {
            Log.e(TAG, "must init message center first");
        }
        return instance;
    }

    protected MessageCenter(Context appContext) {
        this.appContext = appContext;
        handler = new Handler(appContext.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case MSG_RUN_CALL_BACK_ON_MAIN_THREAD:
                        MessageEntity entity = (MessageEntity) msg.obj;
                        dispatchEventOnCurrentThread(entity.msg, entity.callback);
                        break;
                }
            }
        };
    }

    /**
     * start the msg dispatch polling.
     */
    private void startPolling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (polling) {
                    if (msgQueue.size() > 0) {
                        Message msg = msgQueue.poll();
                        innerSend(msg);
                    }
                }
            }
        }).start();
    }

    /**
     * add the listener.
     * 加入Listener.
     *
     * @param messageName
     * @param messageEventListener
     */
    public void addListener(String messageName, MessageEventListener messageEventListener) {
        addListener(messageName, messageEventListener, false);
    }

    /**
     * add the listener sticky.
     * 加入Listener, sticky.
     * @param messageName
     * @param messageEventListener
     */
    public void addListenerSticky(String messageName, MessageEventListener messageEventListener) {
        addListener(messageName, messageEventListener, true);
    }

    public void addListener(String messageName, MessageEventListener messageEventListener, boolean sticky) {
        Log.d(TAG, "add listener Name:" + messageName);

        List<MessageEventListener> list = null;
        if (dicRegisteredEvents.containsKey(messageName)) {
            list = dicRegisteredEvents.get(messageName);
        } else {
            list = new ArrayList<>();
            dicRegisteredEvents.put(messageName, list);
        }

        if (!list.contains(messageEventListener)) {
            list.add(messageEventListener);
        }

        if (sticky) {
            for (Message message : stickyEvents) {
                if (message.name.equals(messageName)) {
                    messageEventListener.onEvent(message);
                    break;
                }
            }
        }
    }

    public void removeListener(String msgName, MessageEventListener msg) {
        if (dicRegisteredEvents.containsKey(msgName)) {
            List<MessageEventListener> eventList = dicRegisteredEvents.get(msgName);
            if (eventList.contains(msg)) {
                eventList.remove(msg);
            }
            if (eventList.size() == 0) {
                dicRegisteredEvents.remove(msgName);
            }
        }
    }

    /**
     * clear all sticky event.
     */
    public void clearStickyEvent() {
        if (stickyEvents != null && stickyEvents.size() > 0) {
            stickyEvents.clear();
        }
    }

    /**
     * dispatch all sticky event.
     */
    public void dispatchAllStickyEvent() {
        if (stickyEvents != null && stickyEvents.size() > 0) {
            for (Message message : stickyEvents) {
                if (message != null) message.send();
            }
        }
    }


    /**
     * send message.
     * 發送消息.
     *
     * @param name
     */
    public void sendEmpty(@Nullable String name) {
        send(name, null, null, null, null);
    }

    /**
     * send message.
     * 發送消息.
     *
     * @param name
     */
    public void sendEmptySticky(@Nullable String name) {
        sendSticky(name, null, null, null, null);
    }

    /**
     * send message.
     * 發送消息.
     *
     * @param name
     * @param sender
     */
    public void sendEmpty(@Nullable String name, @Nullable Object sender) {
        send(name, sender, null, null, null);
    }

    /**
     * send message.
     * 發送消息.
     *
     * @param name        message name
     * @param sender      message sender
     * @param content     message content
     * @param ContentType message content type
     * @param dicPara     message data
     */
    public void send(@NonNull String name, @Nullable Object sender, @Nullable Object content, @Nullable Class<?> ContentType, @Nullable Map<String, Object> dicPara) {
        send(new Message(name, sender, content, ContentType, dicPara));
    }

    /// <summary>
    /// 發送消息.
    /// </summary>
    /// <param name="message"></param>
    public void send(@NonNull final Message message) {
        if (dicRegisteredEvents == null || !dicRegisteredEvents.containsKey(message.name)) return;

        if (pollingMode == PollingMode.POSTING) innerSend(message);
        else if (pollingMode == PollingMode.BACKGROUND) msgQueue.add(message);
    }


    public void sendSticky(@NonNull String name, @Nullable Object sender, @Nullable Object content, @Nullable Class<?> ContentType, @Nullable Map<String, Object> dicPara) {
        sendSticky(new Message(name, sender, content, ContentType, dicPara));
    }


    public void sendSticky(@NonNull final Message message) {
        if (dicRegisteredEvents == null)
            return;

        stickyEvents.add(message);

        if (pollingMode == PollingMode.POSTING) innerSend(message);
        else if (pollingMode == PollingMode.BACKGROUND) {
            msgQueue.add(message);
        }
    }


    private void innerSend(@NonNull Message message) {
        if (!dicRegisteredEvents.containsKey(message.name)) return;

        List<MessageEventListener> list = dicRegisteredEvents.get(message.name);

        for (int i = 0; i < list.size(); i++) {
            final MessageEventListener callback = list.get(i);
            if (callback != null) {
                if (callback instanceof MainThreadEventListener) {
                    dispatchEventOnMainThread(message, callback);

                } else if (callback instanceof NewThreadEventListener) {
                    dispatchEventOnNewThread(message, callback);

                } else {
                    dispatchEventOnCurrentThread(message, callback);
                }
            }
        }
    }


    /**
     * dispatch the specify event on current thread.
     *
     * @param message
     * @param callback
     */
    private void dispatchEventOnCurrentThread(Message message, MessageEventListener callback) {
        callback.onEvent(message);
    }

    /**
     * dispatch the specify event on new thread.
     *
     * @param message
     * @param callback
     */
    private void dispatchEventOnNewThread(final Message message, final MessageEventListener callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                dispatchEventOnCurrentThread(message, callback);
            }
        }, "[MessageCenter thread]");
        thread.start();
    }

    /**
     * dispatch the specify event on BACKGROUND thread.
     *
     * @param message
     * @param callback
     */
    private void dispatchEventOnMainThread(Message message, MessageEventListener callback) {
        android.os.Message msg = new android.os.Message();
        msg.setTarget(handler);
        msg.what = MSG_RUN_CALL_BACK_ON_MAIN_THREAD;
        msg.obj = new MessageEntity(callback, message);
        msg.sendToTarget();
    }

    /**
     * MessageEventListener
     */
    public interface MessageEventListener {

        void onEvent(Message msg);
    }

    /**
     * MainThreadEventListener
     */
    public interface MainThreadEventListener extends MessageEventListener {

    }

    /**
     * NewThreadEventListener
     */
    public interface NewThreadEventListener extends MessageEventListener {

    }

    private class MessageEntity {
        MessageEventListener callback;
        Message msg;

        public MessageEntity(MessageEventListener callback, Message msg) {
            this.callback = callback;
            this.msg = msg;
        }
    }

    /**
     * msg dispatch mode.
     * 消息發布輪詢模式.
     */
    public enum PollingMode {
        // has a thread to hold msg dispatch
        BACKGROUND,
        // use called thread to dispatch msg
        POSTING
    }

    /**
     * msg class
     */
    public static class Message {

        private Map<String, Object> dicData = null;

        /**
         * message name.
         * 消息名稱.
         */
        private String name;

        /**
         * message sender.
         * 消息發送者.
         */
        private Object sender;

        /**
         * message content.
         * 消息內容.
         */
        private Object content;

        /**
         * message content type.
         * 消息內容類型.
         */
        private Class<?> contentType;

        public Object get(String key) {
            if (dicData == null || !dicData.containsKey(key))
                return null;
            return dicData.get(key);
        }

        public void set(String key, Object value) {

            if (dicData == null)
                dicData = new HashMap<>();

            else if (dicData.containsKey(key))
                dicData.put(key, value);

            else
                dicData.put(key, value);
        }


        public Message(String name, Object sender, Object content, Class<?> contentType, Map<String, Object> dicParas) {
            dicData = new HashMap<>();
            this.name = name;
            this.sender = sender;
            this.content = content;
            this.contentType = contentType;

            if (dicParas != null && dicParas.size() > 0) {

                for (Map.Entry entry : dicParas.entrySet()) {
                    set((String) entry.getKey(), entry.getValue());
                }
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getSender() {
            return sender;
        }

        public void setSender(Object sender) {
            this.sender = sender;
        }

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }

        public Class<?> getContentType() {
            return contentType;
        }

        public void setContentType(Class<?> contentType) {
            this.contentType = contentType;
        }

        /**
         * add content to message.
         * 向消息中加入內容(key Value).
         * @param key
         * @param value
         */
        public void add(String key, Object value) {
            set(key, value);
        }

        /**
         * send message.
         * 發送Message.
         */
        public void send() {
            MessageCenter.getInstance().send(this);
        }

        public <T extends Object> T getContent(Class<T> clazz) {
            if (content != null)
                return clazz.cast(this.content);

            return null;
        }


    }

}
