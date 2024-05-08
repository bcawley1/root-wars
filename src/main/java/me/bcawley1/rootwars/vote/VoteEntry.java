package me.bcawley1.rootwars.vote;

public record VoteEntry<T extends Votable>(T item, int votes) implements Comparable<VoteEntry<T>> {
    @Override
    public int compareTo(VoteEntry o) {
        return "%s%s".formatted(votes, item.getName()).compareTo("%s%s".formatted(o.votes, o.item.getName()));
    }
}
