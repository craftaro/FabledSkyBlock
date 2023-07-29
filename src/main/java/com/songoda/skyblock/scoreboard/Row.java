package com.songoda.skyblock.scoreboard;

import com.songoda.core.utils.TextUtils;

import java.util.List;

class Row {
    private final int interval;
    private final List<String> lines;
    private String line;
    private int current;
    private int count;

    public boolean static_line;

    Row(List<String> lines, int interval) {
        this.lines = lines;
        this.interval = interval;
        this.current = 0;
        this.count = 0;

        this.static_line = interval < 0 || lines.isEmpty() || lines.size() <= 1;

        if (lines.isEmpty()) {
            this.line = "";
        } else {
            this.line = TextUtils.formatText(lines.get(this.current));
        }
    }

    void update() {
        if (!this.static_line) {
            if (this.count >= this.interval) {
                this.count = 0;
                this.current++;
                if (this.current >= this.lines.size())
                    this.current = 0;
                this.line = TextUtils.formatText(this.lines.get(this.current));
            } else {
                this.count++;
            }
        }
    }

    String getLine() {
        return this.line;
    }
}
