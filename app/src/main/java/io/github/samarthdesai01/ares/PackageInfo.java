package io.github.samarthdesai01.ares;

public class PackageInfo {
    String packageName = null;
    String packageLink = null;
    String packageStatus = null;

    public PackageInfo(String name, String link, String status){
        packageName = name;
        packageLink = link;
        packageStatus = status;
    }

    public PackageInfo(){
    }

    @Override
    public String toString() {
        return "Package Name: " + packageName + "\n" +
                "Package Link: " + packageLink + "\n" +
                "Package Status " + packageStatus + "\n";
    }
}
