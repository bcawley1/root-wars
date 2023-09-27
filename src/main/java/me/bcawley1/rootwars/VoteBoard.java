package me.bcawley1.rootwars;

import java.util.*;

public class VoteBoard {
    private Map<String, Integer> votes;
    private Map<UUID, String> playerVotes;

    public VoteBoard() {
        for(GameMap map : GameMap.getMaps().values()){
            votes.put(map.getMapName(), 0);
        }
    }
    public void addVote(String map, UUID player){
        votes.merge(map, 1, Integer::sum);
        playerVotes.put(player, GameMap.MaptoDisplayName(map));
    }
    public Map<Integer, VoteEntry> getBoard(){
        List<VoteEntry> voteEntries = new ArrayList<>();
        Map<Integer, VoteEntry> board = new HashMap<>();
        votes.forEach((key, value) -> voteEntries.add(new VoteEntry(GameMap.MaptoDisplayName(key), value)));
        Collections.sort(voteEntries);
        for(int i = 0; i < voteEntries.size(); i++){
            //fix here
        }

    }
}
