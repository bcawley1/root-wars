package me.bcawley1.rootwars;

import org.jetbrains.annotations.NotNull;

public class VoteEntry implements Comparable<VoteEntry> {
    int votes;
    String name;
    public VoteEntry(String name) {
        this.name = name;
        this.votes=0;
    }
    public VoteEntry(String name, int votes) {
        this.name = name;
        this.votes=votes;
    }

    public void addVote(){
        votes++;
    }

    public int getVotes() {
        return votes;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull VoteEntry o) {
        return "%s%s".formatted(votes, name).compareTo("%s%s".formatted(o.votes, o.name));
    }
}
