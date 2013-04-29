package com.synergy.android.timetable.domains;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Kind {
    LECTURE {
        @Override
        public String toString() {
            return "lecture";
        }
    },
    
    PRACTICE {
        @Override
        public String toString() {
            return "practice";
        }
    },
    
    LAB {
        @Override
        public String toString() {
            return "laboratory";
        }
    },
    
    COURSEWORK {
        @Override
        public String toString() {
            return "research";
        }
    },
    
    COURSEPROJECT {
        @Override
        public String toString() {
            return "design";
        }
    },
    
    FINALEXAM {
        @Override
        public String toString() {
            return "exam";
        }
    },
    
    PASSFAILEXAM {
        @Override
        public String toString() {
            return "test";
        }
    },
    
    UNKNOWN {
        @Override
        public String toString() {
            return "unknown";
        }
    };
    
    private static Map<String, Kind> kinds;
    static {
        Map<String, Kind> temp = new HashMap<String, Kind>();
        temp.put("lecture", LECTURE);
        temp.put("practice", PRACTICE);
        temp.put("laboratory", LAB);
        temp.put("research", COURSEWORK);
        temp.put("design", COURSEPROJECT);
        temp.put("exam", FINALEXAM);
        temp.put("test", PASSFAILEXAM);
        temp.put("unknown", UNKNOWN);
        kinds = Collections.unmodifiableMap(temp);
    }
    
    public static Kind getKind(String kind) {
        return kinds.get(kind);
    }
}
