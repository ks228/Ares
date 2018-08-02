package io.github.samarthdesai01.ares;

public class PackageInfo {
    String packageName = null;
    String packageLink = null;
    String packagePrimaryStatus = null;
    String packageShortStatus = null;

    public PackageInfo(String name, String link, String status, String shortStatus){
        packageName = name;
        packageLink = link;
        packagePrimaryStatus = status;
        packageShortStatus = shortStatus;
    }

    public PackageInfo(){
    }

    @Override
    public String toString() {
        return "Package Name: " + packageName + "\n" +
                "Package Link: " + packageLink + "\n" +
                "Package Status " + packagePrimaryStatus + "\n";
    }
}
