package me.sedri.slayers.Data;

import java.util.ArrayList;

public class SlayerLevel {
    private int xp;
    private ArrayList<String> rewards;
    private ArrayList<String> commands;
    private ArrayList<String> permissions;

    public SlayerLevel(int xp, ArrayList<String> rewards, ArrayList<String> commands, ArrayList<String> permissions){
        this.xp = xp;
        this.rewards = rewards;
        this.permissions = permissions;
        this.commands = commands;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }

    public ArrayList<String> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<String> rewards) {
        this.rewards = rewards;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
