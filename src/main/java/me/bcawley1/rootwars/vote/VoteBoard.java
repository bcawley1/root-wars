package me.bcawley1.rootwars.vote;

import me.bcawley1.rootwars.GameMap;
import me.bcawley1.rootwars.gamemodes.GameMode;

import java.util.*;

public class VoteBoard {
    private Map<String, Integer> votes;
    private Map<UUID, String> playerVotes;
    private VoteType voteType;

    public VoteBoard(VoteType voteType) {
        this.voteType = voteType;
        votes = new HashMap<>();
        playerVotes = new HashMap<>();
        if(voteType.equals(voteType.MAP)) {
            for (GameMap map : GameMap.getMaps().values()) {
                votes.put(map.getMapName(), 0);
            }
        } else if(voteType.equals(VoteType.GAMEMODE)){
            for (GameMode mode : GameMode.getGameModes().values()) {
                votes.put(mode.getGameModeName(), 0);
            }
        }
    }
    public void addVote(String map, UUID player){
        votes.merge(voteType.equals(voteType.MAP) ? GameMap.DisplaytoMapName(map) : map, 1, Integer::sum);
        playerVotes.put(player, map);
    }
    public Map<Integer, VoteEntry> getBoard(){
        List<VoteEntry> voteEntries = new ArrayList<>();
        Map<Integer, VoteEntry> board = new HashMap<>();
        votes.forEach((key, value) -> {
            if(value!=0) {
                voteEntries.add(new VoteEntry(voteType.equals(voteType.MAP) ? GameMap.MaptoDisplayName(key) : key, value));
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
        return size;
    }
    public String getVotedMap(UUID uuid){
        return playerVotes.get(uuid);
    }
    public int playersVoted(){
        return playerVotes.size();
    }
}
