package org.cybercrowd.activity.service;

import org.cybercrowd.activity.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ContractPlayerService {

    private Logger logger = LoggerFactory.getLogger(ContractPlayerService.class);

    @Value("${web3j.ethereum.chain-id}")
    private String chainId;

    @Autowired
    Web3j web3j;

    public ArrayList<Player> getLastRoundPlayers(String contract) {
        try {
            Function function = new Function("getLastRoundPlayers", Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Player>>() {
                    }));
            Transaction transaction = Transaction.createEthCallTransaction(
                    "0x52129424797b23183934A3684B2f05a465005c4A",contract, FunctionEncoder.encode(function));
            EthCall send = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(send.getValue(), function.getOutputParameters());

            if(null != results && results.size() > 0) {
                ArrayList<Player> players = (ArrayList<Player>) results.get(0).getValue();
                return players;
            }
        }catch (Exception e){
            logger.error("拉取合约数据异常:{}",e.getMessage(),e);
        }
        return null;
    }

    public ArrayList<Player> getLastRoundWinners(String contract) {
        try {
            Function function = new Function("getLastRoundWinners", Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Player>>() {
                    }));
            Transaction transaction = Transaction.createEthCallTransaction(
                    "0x52129424797b23183934A3684B2f05a465005c4A",contract, FunctionEncoder.encode(function));
            EthCall send = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(send.getValue(), function.getOutputParameters());
            if(null != results && results.size() > 0) {
                ArrayList<Player> players = (ArrayList<Player>) results.get(0).getValue();
                return players;
            }
        }catch (Exception e){
            logger.error("拉取合约数据异常:{}",e.getMessage(),e);
        }
        return null;
    }


    public ArrayList<Player> getPlayers(String contract){
        try {
            Function function = new Function("getPlayers", Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Player>>() {
                    }));
            Transaction transaction = Transaction.createEthCallTransaction(
                    "0xd6C35Ed78035Ee207d711f0CCbE0941A38184B6F",contract, FunctionEncoder.encode(function));
            EthCall send = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(send.getValue(), function.getOutputParameters());
            if(null != results && results.size() > 0) {
                ArrayList<Player> players = (ArrayList<Player>) results.get(0).getValue();
                return players;
            }
        }catch (Exception e){
            logger.error("拉取合约数据异常:{}",e.getMessage(),e);
        }
        return null;
    }

    public String drawLottery(String contract){

        try {
            Credentials credentials = Credentials.create("");

            BigInteger nonce = web3j.ethGetTransactionCount(
                    "0xd6C35Ed78035Ee207d711f0CCbE0941A38184B6F", DefaultBlockParameterName.LATEST).send().getTransactionCount();
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            logger.info("gas price:{}",gasPrice.toString());

            Function function = new Function("drawLottery", Arrays.<Type> asList(),
                    Collections.<TypeReference<?>> emptyList());

            EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(Transaction.createEthCallTransaction("0xd6C35Ed78035Ee207d711f0CCbE0941A38184B6F",
                    contract,FunctionEncoder.encode(function))).send();

            BigInteger gasLimit = ethEstimateGas.getAmountUsed();

            logger.info("gas limit:{}",gasLimit.toString());

            BigInteger newLimit = new BigDecimal(gasLimit).multiply(new BigDecimal("1.3")).toBigInteger();

            logger.info("new gas limit:{}",newLimit.toString());

            RawTransaction rawTransaction = RawTransaction.
                    createTransaction(nonce,gasPrice,newLimit,contract, FunctionEncoder.encode(function));

            //使用Credentials对象对RawTransaction对象进行签名
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,Long.valueOf(chainId), credentials);
            String signHexValue = Numeric.toHexString(signedMessage);

            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signHexValue).send();

            if(null == ethSendTransaction.getError()
                    && StringUtils.hasText(ethSendTransaction.getTransactionHash())){
                logger.info("彩票开奖,Web3j,交易Hash:{}",ethSendTransaction.getTransactionHash());
            }else{
                logger.info("彩票开奖,Web3j请求合约执行错误: {} {}",ethSendTransaction.getError().getCode(),ethSendTransaction.getError().getMessage());
                return "FAIL";
            }

//            Function function = new Function("drawLottery", Arrays.<Type>asList(),
//                    Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Player>>() {
//                    }));
//            Transaction transaction = Transaction.createEthCallTransaction(
//                    "0x52129424797b23183934A3684B2f05a465005c4A",contract, FunctionEncoder.encode(function));
//            EthCall send = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
//
//            List<Type> results = FunctionReturnDecoder.decode(send.getValue(), function.getOutputParameters());
        }catch (Exception e){
            logger.error("彩票开奖,调用合约异常:{}",e.getMessage(),e);
            return "FAIL";
        }
        return "OK";
    }

}
