package me.bcawley1.rootwars.vote;

public class VoteEntry implements Comparable<VoteEntry> {
    private int votes;
    private String name;
    public VoteEntry(String name) {
        this.name = name;
        this.votes=0;
    }
    public VoteEntry(String name, int votes) {
        this.name = name;
        this.votes=votes;
    }

    public int getVotes() {
        return votes;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(VoteEntry o) {
        return "%s%s".formatted(votes, name).compareTo("%s%s".formatted(o.votes, o.name));
    }
}