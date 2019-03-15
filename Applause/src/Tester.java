import java.util.*;

public class Tester {
    private int testerId;
    private String firstName;
    private String lastName;
    private String country;
    private String lastLogin;
    private Map<String, Integer> experience; // key: deviceName => value: bugs count
    private Set<String> devices;

    public Tester(int testerId, String firstName, String lastName, String country, String lastLogin) {
        this.testerId = testerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.lastLogin = lastLogin;
        this.experience = new HashMap<>();
        this.devices = new HashSet<>();
    }

    public int getTesterId() {
        return testerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getCountry() {
        return country;
    }


    public String getLastLogin() {
        return lastLogin;
    }

    public void addBugExperience(String deviceName) {
        if (experience.containsKey(deviceName)) {
            int count = experience.get(deviceName);
            experience.put(deviceName, ++count);

        } else {
            experience.put(deviceName, 1);
        }
    }

    public int getBugExperience(String deviceName) {
        if (!experience.containsKey(deviceName)) {
            return 0;
        }
        return experience.get(deviceName);
    }

    public void addDevice(String deviceName) {
        devices.add(deviceName);
    }

    public Set getDevices() {
        return this.devices;
    }

}
