package com.dormantmc.teams;

public enum Rank {
    OWNER(3), ADMIN(2), MOD(1), MEMBER(0);

    private int level;
    
    private Rank(int level) {
        this.level = level;
    }
    
    public boolean atLeast(Rank rank) {
        return this.level >= rank.level;
    }
    
    public Rank next() {
        switch (this) {
        case MEMBER:
            return MOD;
        case MOD:
            return ADMIN;
        case ADMIN:
            return OWNER;
        default:
            return OWNER;
        }
    }

    public Rank previous() {
        switch (this) {
        case OWNER:
            return ADMIN;
        case ADMIN:
            return MOD;
        case MOD:
            return MEMBER;
        default:
            return MEMBER;
        }
    }

}
