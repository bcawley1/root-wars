package me.bcawley1.rootwars.vote;

import java.util.*;

public class VoteBoard<T extends Votable> {
    private final Map<T, Integer> votes;
    private final Map<UUID, T> playerVotes;

    public VoteBoard(List<T> items) {
        votes = new HashMap<>();
        playerVotes = new HashMap<>();
        items.forEach(i -> votes.put(i, 0));
    }

    public void addVote(T item, UUID player) {
        votes.put(item, votes.getOrDefault(item, 0) + 1);
        playerVotes.put(player, item);
    }

    public List<VoteEntry<T>> getBoard() {
        List<VoteEntry<T>> voteEntries = new ArrayList<>();
        votes.forEach((key, value) -> voteEntries.add(new VoteEntry<>(key, value)));
        Collections.sort(voteEntries);
        return voteEntries;
    }

    public int getBoardSize() {
        int size = 0;
        for (int v : votes.values()) {
            if (v != 0) {
                size++;
            }
        }
        return size;
    }

    public T getVotedItem(UUID uuid) {
        return playerVotes.get(uuid);
    }

    public int playersVoted() {
        return playerVotes.size();
    }

    @Override
    public String toString() {
        return "VoteBoard{" + "votes=" + votes +"}";
    }
}