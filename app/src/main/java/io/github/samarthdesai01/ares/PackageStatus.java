package io.github.samarthdesai01.ares;

public class PackageStatus {
    public String primaryStatus = null;
    public String shortStatus = null;

    public String getPrimaryStatus(){
        return primaryStatus;
    }

    public String getShortStatus(){
        return shortStatus;
    }

    @Override
    public String toString(){
        return "Primary Status: " + primaryStatus + " Short Status: " + shortStatus;
    }
}
