package me.bcawley1.rootwars;

import java.util.*;

public class VoteBoard {
    private Map<String, Integer> votes;
    private Map<UUID, String> playerVotes;

    public VoteBoard() {
        votes = new HashMap<>();
        playerVotes = new HashMap<>();
        for(GameMap map : GameMap.getMaps().values()){
            votes.put(map.getMapName(), 0);
        }
    }
    public void addVote(String map, UUID player){
        votes.merge(GameMap.DisplaytoMapName(map), 1, Integer::sum);
        playerVotes.put(player, map);
    }
    public Map<Integer, VoteEntry> getBoard(){
        List<VoteEntry> voteEntries = new ArrayList<>();
        Map<Integer, VoteEntry> board = new HashMap<>();
        votes.forEach((key, value) -> {
            if(value!=0) {
                voteEntries.add(new VoteEntry(GameMap.MaptoDisplayName(key), value));
            }
        });
        Collections.sort(voteEntries);
        for(int i = 0; i < voteEntries.size(); i++){
            board.put(i, voteEntries.get(i));
        }
        return board;
    }
    public int getBoardSize(){
        int size = 0;
        for(int v : votes.values()){
            if(v!=0){
                size++;
            }
        }
        System.out.println("size:"+size);
        return size;
    }
    public String getVotedMap(UUID uuid){
        return playerVotes.get(uuid);
    }
    public int playersVoted(){
        return playerVotes.size();
    }
}
