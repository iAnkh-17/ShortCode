package uniqid;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ankh
 * @Description 当前实现并未使用数据中心id
 * @createTime 2022/9/26 16:37
 */
public class IdGenerator {

    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private final long twepoch = 1664181720586L;
    /**
     * 机器标识位数
     */
    private final long workerIdBits = 5L;

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /**
     * 毫秒内自增位
     */
    private final long sequenceBits = 14L;

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private final long workerId;

    /**
     * 并发控制
     */
    private long sequence = 0L;
    /**
     * 上次生产 ID 时间戳
     */
    private long lastTimestamp = -1L;

    private long last = 0L;

    public IdGenerator() {
        this.workerId = getMaxWorkerId();
    }

    public IdGenerator(long workerId) {
        this.workerId = workerId;
    }

    /**
     * <p>
     * 获取 maxWorkerId
     * </p>
     */
    protected long getMaxWorkerId() {
        StringBuilder mpid = new StringBuilder();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name!=null) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + jvmPid 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 0 - 9 随机数
            sequence = ThreadLocalRandom.current().nextLong(0, 9);
        }
        // 1,41,5,14,3
        lastTimestamp = timestamp;
        // 最后一位数不能为0
        last = ThreadLocalRandom.current().nextLong(1, 7);
        return ((timestamp - twepoch) << 22)
                | (workerId << 17)
                | (sequence << 3)
                | last;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
