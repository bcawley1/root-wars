package me.bcawley1.rootwars.vote;

import java.util.*;

public class VoteBoard<T extends Votable> {
    private Map<T, Integer> votes;
    private Map<UUID, String> playerVotes;

    public VoteBoard(List<T> items) {
        votes = new HashMap<>();
        playerVotes = new HashMap<>();
        for (T item : items) {
            votes.put(item, 0);
        }
    }

    public void addVote(String item, UUID player) {
        votes.merge(item, 1, Integer::sum);
        playerVotes.put(player, item);
    }

    public List<VoteEntry> getBoard() {
        List<VoteEntry> voteEntries = new ArrayList<>();
        votes.forEach((key, value) -> {
            if (value != 0) {
                voteEntries.add(new VoteEntry(key, value));
            }
        });
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

    public String getVotedItem(UUID uuid) {
        return playerVotes.get(uuid);
    }

    public int playersVoted() {
        return playerVotes.size();
    }
}
