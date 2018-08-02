package io.github.samarthdesai01.ares;

public class PackageStatus {
    public String primaryStatus = null;
    public String shortStatus = null;

    public String getPrimaryStatus(){
        return primaryStatus;
    }

    @Override
    public String toString(){
        return "Primary Status: " + primaryStatus + " Short Status: " + shortStatus;
    }
}
