package org.cybercrowd.activity.model;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;

import java.io.Serializable;

public class Player extends StaticStruct implements Serializable {

    public Address playerAddress;
    public Address inviterAddress;
    public Uint256 round;
    public Uint64 roundIndex;
    public Uint256 amount;
    public Uint256 randomNum;
    public Uint256 bonus;
    public Bool isWinner;
    public Uint256 timestamp;

    public Player(Address playerAddress, Address inviterAddress, Uint256 round, Uint64 roundIndex,
                  Uint256 amount, Uint256 randomNum, Uint256 bonus, Bool isWinner, Uint256 timestamp) {
        super(new Type[]{playerAddress,inviterAddress,round,randomNum, amount, randomNum, bonus,isWinner,timestamp});
        this.playerAddress = playerAddress;
        this.inviterAddress = inviterAddress;
        this.round = round;
        this.roundIndex = roundIndex;
        this.amount = amount;
        this.randomNum = randomNum;
        this.bonus = bonus;
        this.isWinner = isWinner;
        this.timestamp = timestamp;
    }
}
