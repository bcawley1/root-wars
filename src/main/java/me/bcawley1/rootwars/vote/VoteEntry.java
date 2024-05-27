package me.bcawley1.rootwars.vote;

public record VoteEntry<T extends Votable>(T item, int votes) implements Comparable<VoteEntry<T>> {
    @Override
    public int compareTo(VoteEntry o) {
        return o.votes - votes;
    }
}