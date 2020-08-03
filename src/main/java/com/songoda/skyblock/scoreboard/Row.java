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

    public Row(List<String> lines, int interval) {
        this.lines = lines;
        this.interval = interval;
        this.current = 0;
        this.count = 0;
        
        if(interval < 0) {
            static_line = true;
        } else {
            static_line = false;
        }

        if(lines.isEmpty()) {
            line = "";
        } else {
            line = TextUtils.formatText(lines.get(current));
        }
    }

    public void update() {
        if (!static_line && !lines.isEmpty()) {
            if (count >= interval) {
                count = 0;
                current++;
                if (current >= lines.size())
                    current = 0;
                line = TextUtils.formatText(lines.get(current));
            } else {
                count++;
            }
        }
    }

    public String getLine() {
        return this.line;
    }
    
}
