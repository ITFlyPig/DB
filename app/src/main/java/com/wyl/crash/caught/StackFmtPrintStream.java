package com.wyl.crash.caught;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author : yuelinwang
 * @time : 6/16/21
 * @desc : 栈格式化的PrintStream
 */
public class StackFmtPrintStream extends PrintStream {
    private StringBuilder mBuilder;
    public StackFmtPrintStream(OutputStream out) {
        super(out);
        init();
    }

    public StackFmtPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
        init();
    }

    public StackFmtPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
        init();
    }

    public StackFmtPrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
        init();
    }

    public StackFmtPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
        init();
    }

    public StackFmtPrintStream(File file) throws FileNotFoundException {
        super(file);
        init();
    }

    public StackFmtPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
        init();
    }

    private void init() {
        mBuilder = new StringBuilder();
    }

    @Override
    public void println(int x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(char x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(long x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(float x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(char[] x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(double x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(Object x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(String x) {
        synchronized (this) {
            mBuilder.append(x);
            mBuilder.append("\n");
        }
    }

    @Override
    public void println(boolean x) {
        synchronized (this) {
            mBuilder.append(String.valueOf(x));
            mBuilder.append("\n");
        }
    }

    @Override
    public void print(boolean b) {
        synchronized (this) {
            mBuilder.append(String.valueOf(b));
        }
    }

    @Override
    public void print(char c) {
        synchronized (this) {
            mBuilder.append(String.valueOf(c));
        }
    }

    @Override
    public void print(int i) {
        synchronized (this) {
            mBuilder.append(String.valueOf(i));
        }
    }

    @Override
    public void print(long l) {
        synchronized (this) {
            mBuilder.append(String.valueOf(l));
        }
    }

    @Override
    public void print(float f) {
        synchronized (this) {
            mBuilder.append(String.valueOf(f));
        }
    }

    @Override
    public void print(double d) {
        synchronized (this) {
            mBuilder.append(String.valueOf(d));
        }
    }

    @Override
    public void print(char[] s) {
        synchronized (this) {
            mBuilder.append(String.valueOf(s));
        }
    }

    @Override
    public void print(String s) {
        if (s == null) {
            return;
        }
        synchronized (this) {
            mBuilder.append(s);
        }
    }

    @Override
    public void print(Object obj) {
        synchronized (this) {
            mBuilder.append(String.valueOf(obj));
        }
    }

    /**
     * 获取格式化之后的栈
     * @return
     */
    public StringBuilder getStackFmt() {
        return mBuilder;
    }
}
