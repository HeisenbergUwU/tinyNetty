package io.donkey.channel;

public interface ChannelFactory<T extends Channel> {
    /**
     * Creates a new channel.
     */
    T newChannel();
}
