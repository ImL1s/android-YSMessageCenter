package org.iml1s.messagecenter.wrapper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ImL1s on 2017/4/18.
 * <p>
 * DESC:
 */

public interface IMessageManager {
    int MSG_RUN_CALL_BACK_ON_MAIN_THREAD = 0;

    void init(Context appContext);

    void init(Context appContext, PollingMode pollingMode);

    void addListener(String messageName, MessageEventListener messageEventListener);

    void addListenerSticky(String messageName, MessageEventListener messageEventListener);

    void addListener(String messageName, MessageEventListener messageEventListener, boolean sticky);

    void removeListener(String msgName, MessageEventListener msg);

    void clearStickyEvent();

    void dispatchAllStickyEvent();

    void sendEmpty(@Nullable String name);

    void sendEmptySticky(@Nullable String name);

    void sendEmpty(@Nullable String name, @Nullable Object sender);

    void send(@NonNull String name, @Nullable Object sender, @Nullable Object content, @Nullable Class<?> ContentType, @Nullable Map<String, Object> dicPara);

    /// <summary>
    /// 發送消息.
    /// </summary>
    /// <param name="message"></param>
    void send(@NonNull Message message);

    void sendSticky(@NonNull String name, @Nullable Object sender, @Nullable Object content, @Nullable Class<?> ContentType, @Nullable Map<String, Object> dicPara);

    void sendSticky(@NonNull Message message);


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

        private IMessageManager messageManager;

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

        public Message() {
        }

        public Message(String name, Object sender, Object content, Class<?> contentType, Map<String, Object> dicParas, IMessageManager messageManager) {
            this.messageManager = messageManager;
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

        public IMessageManager getMessageManager() {
            return this.messageManager;
        }

        public void setMessageManager(IMessageManager messageManager) {
            this.messageManager = messageManager;
        }

        public Map<String, Object> getDicData() {
            return this.dicData;
        }

        /**
         * add content to message.
         * 向消息中加入內容(key Value).
         *
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
            messageManager.send(this);
        }

        public <T extends Object> T getContent(Class<T> clazz) {
            if (content != null)
                return clazz.cast(this.content);

            return null;
        }
    }
}
