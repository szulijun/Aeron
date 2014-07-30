/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.aeron.common.command;

import uk.co.real_logic.aeron.common.Flyweight;

import java.nio.ByteOrder;

import static uk.co.real_logic.aeron.common.BitUtil.SIZE_OF_INT;

/**
 * Control message flyweight for any message that needs to represent a connection
 *
 * <p>
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          Session ID                           |
 * +---------------------------------------------------------------+
 * |                          Stream ID                            |
 * +---------------------------------------------------------------+
 * |      Channel   Length       |   Channel                     ...
 * |                                                             ...
 * +---------------------------------------------------------------+
 */
public class ConnectionMessageFlyweight extends Flyweight
{
    private static final int SESSION_ID_OFFSET = 0;
    private static final int STREAM_ID_FIELD_OFFSET = 4;
    private static final int CHANNEL_OFFSET = 8;

    private int lengthOfChannel;

    /**
     * return session id field
     * @return session id field
     */
    public int sessionId()
    {
        return atomicBuffer().getInt(offset() + SESSION_ID_OFFSET, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * set session id field
     * @param sessionId field value
     * @return flyweight
     */
    public ConnectionMessageFlyweight sessionId(final int sessionId)
    {
        atomicBuffer().putInt(offset() + SESSION_ID_OFFSET, sessionId, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    /**
     * return stream id field
     *
     * @return stream id field
     */
    public int streamId()
    {
        return atomicBuffer().getInt(offset() + STREAM_ID_FIELD_OFFSET, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * set stream id field
     *
     * @param streamId field value
     * @return flyweight
     */
    public ConnectionMessageFlyweight streamId(final int streamId)
    {
        atomicBuffer().putInt(offset() + STREAM_ID_FIELD_OFFSET, streamId, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    /**
     * return channel field
     *
     * @return channel field
     */
    public String channel()
    {
        final int channelOffset = offset() + CHANNEL_OFFSET;
        final int length = atomicBuffer().getInt(channelOffset, ByteOrder.LITTLE_ENDIAN);
        lengthOfChannel = SIZE_OF_INT + length;
        return atomicBuffer().getString(channelOffset, length);
    }

    /**
     * set channel field
     *
     * @param channel field value
     * @return flyweight
     */
    public ConnectionMessageFlyweight channel(final String channel)
    {
        lengthOfChannel = stringPut(offset() + CHANNEL_OFFSET, channel, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    /**
     * Get the length of the current message
     *
     * NB: must be called after the data is written in order to be accurate.
     *
     * @return the length of the current message
     */
    public int length()
    {
        return CHANNEL_OFFSET + lengthOfChannel;
    }
}