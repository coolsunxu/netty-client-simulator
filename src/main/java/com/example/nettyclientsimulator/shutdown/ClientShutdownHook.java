package com.example.nettyclientsimulator.shutdown;

/**
 * @author sunxu
 */

public interface ClientShutdownHook extends Runnable {
    /**
     * The name of the shutdown hook. This name is used for logging purposes
     *
     * @return the name of the Example shutdown hook
     */
    String name();

    /**
     * The {@link Priority} of the shutdown hook.
     *
     * @return the {@link Priority} of the shutdown hook.
     */
    default Priority priority() {
        return Priority.DOES_NOT_MATTER;
    }

    enum Priority {
        /**
         * 最高等级
         */
        FIRST(Integer.MAX_VALUE),

        /**
         * 严重的
         */
        CRITICAL(1_000_000),

        /**
         * 非常高
         */
        VERY_HIGH(500_000),

        /**
         * 高
         */
        HIGH(100_000),

        /**
         * 中等
         */
        MEDIUM(50_000),

        /**
         * 低等级
         */
        LOW(10_000),

        /**
         * 非常低
         */
        VERY_LOW(5_000),

        /**
         * 一点不重要
         */
        DOES_NOT_MATTER(Integer.MIN_VALUE);

        private final int value;

        Priority(final int value) {

            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name() + " (" + getValue() + ")";
        }
    }
}
