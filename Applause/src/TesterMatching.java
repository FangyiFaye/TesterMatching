
import java.util.*;

public class TesterMatching {
    private List<Tester> testers;
    private Map<Integer, String> deviceMap;
    private Set<String> countries;


    public TesterMatching() {
        this.init();
    }

    public static void main(String[] args) {

        TesterMatching testerMatching = new TesterMatching();

        //get the criteria countries
        List<String> searchCountries = testerMatching.scanInputCountries();
        while(!testerMatching.validateInputCountries(searchCountries)) {
            searchCountries = testerMatching.scanInputCountries();
        }

        //get the criteria devices
        List<String> searchDevices = testerMatching.scanInputDevices();
        while(!testerMatching.validateInputDevices(searchDevices)) {
            searchDevices = testerMatching.scanInputDevices();
        }

        //get all matched result
        List<MatchResult> results = testerMatching.matchTester(searchCountries, searchDevices);

        //sort by experience
        Collections.sort(results, Collections.reverseOrder());

        //output the result by printing
        testerMatching.prettyPrintResult(searchCountries, searchDevices, results);

    }

    /**
     * List available countries and scan the input countries
     * @return
     */
    private List<String> scanInputCountries() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the country(s) for tester matching criteria, please use comma to seperate");
        System.out.println("Available countries: ");
        for (String country: countries) {
            System.out.println(country);
        }
        String res = scanner.nextLine();
        return Arrays.asList(res.split(","));

    }

    /**
     * List available devices and scan the input devices
     * @return
     */
    private List<String> scanInputDevices() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the devices for tester matching criteria, please use comma to seperate");
        System.out.println("Available devices: ");
        for (String device: deviceMap.values()) {
            System.out.println(device);
        }
        String res = scanner.nextLine();
        return Arrays.asList(res.split(","));
    }

    /**
     *
     * @param searchCountries
     * @return
     */
    private boolean validateInputCountries(List<String> searchCountries) {
        if (searchCountries.contains("ALL")) {
            return true;
        }
        for (String country: searchCountries) {
            if(!this.countries.contains(country)) {
                System.out.println("Invalid country name: " + country);
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param searchDevices
     * @return
     */
    private boolean validateInputDevices(List<String> searchDevices) {
        if(searchDevices.contains("ALL")){
            return true;
        }
        for (String device: searchDevices) {
            if(!this.deviceMap.values().contains(device)) {
                System.out.println("Invalid device name " + device);
                return false;
            }
        }
        return true;
    }

    /**
     * Initialize by loading all CSV files and convert data into object
     */
    private void init() {
        try {
            deviceMap = parseDevices();
            testers = parseTesters();
            countries = getTesterCountries(testers);

        } catch (Exception e) {
            System.err.println("loading CSV File error");
            e.printStackTrace();
        }

    }

    /**
     * Get matched testers and corresponding experience by provided search criteria
     * @param countries
     * @param devices
     * @return
     */
    private List<MatchResult> matchTester(List<String> countries, List<String> devices) {
        //filter testers by search criteria: country
        List<Tester> matchedTester = filterTestersByCountry(testers, countries);

        //filter testers by search criteria: device
        matchedTester = filterTestersByDevices(matchedTester, devices);


        //if device is ALL, replace devices list with all devices names
        if (devices.contains("ALL")) {
             devices = new ArrayList<>(deviceMap.values());
        }
        List<MatchResult> results = new ArrayList<>();

         //calculate result based on selected devices
        for (Tester tester: matchedTester) {
            int exp = 0;
            for (String deviceName : devices) {
                exp = exp + tester.getBugExperience(deviceName);
            }
            results.add(new MatchResult(tester, exp));
        }
        return results;

    }


    /**
     * Parse testers.csv, tester_devices.csv and bugs.csv
     * Encapsulate the data into Tester object
     * @return
     */
    private List<Tester> parseTesters() {
        List<Tester> res = new ArrayList<>();
        List<List<String>> testerRes = CsvParser.parseCSV("conf/testers.csv");
        List<List<String>> testerDevices = CsvParser.parseCSV("conf/tester_device.csv");
        List<List<String>> bugRes = CsvParser.parseCSV("conf/bugs.csv");


        for (List<String> row : testerRes) {

            int testerId = Integer.valueOf(row.get(0));
            String firstName = row.get(1);
            String lastName = row.get(2);
            String country = row.get(3);
            String lastLogin = row.get(4);
            Tester tester = new Tester(testerId, firstName, lastName, country, lastLogin);

            //Add supported devices of the tester
            for(List<String> testerDevice : testerDevices) {
                if (testerId == Integer.valueOf(testerDevice.get(0))) {
                    int deviceId = Integer.valueOf(testerDevice.get(1));
                    tester.addDevice(deviceMap.get(deviceId));
                }
            }

            //Add bug experience(Did how many bugs of each device) of the tester
            for(List<String> bug : bugRes) {
                int deviceId = Integer.valueOf(bug.get(1));
                int bugTesterId = Integer.valueOf(bug.get(2));

                if (testerId == bugTesterId) {
                    tester.addBugExperience(deviceMap.get(deviceId));
                }
            }

            res.add(tester);

        }
        return res;

    }

    /**
     * Parse devices from devices.csv and save into HashMap
     * @return
     */
    private HashMap<Integer, String> parseDevices() {
        HashMap<Integer, String> map = new HashMap<>();

        List<List<String>> res = CsvParser.parseCSV("conf/devices.csv");
        for (List<String> row : res) {
            int deviceId = Integer.valueOf(row.get(0));
            String deviceName = row.get(1);
            map.put(deviceId, deviceName);
        }
        return map;
    }

    /**
     * get all countries of testers and save them into hash set
     * @param testers
     * @return
     */
    private Set<String> getTesterCountries(List<Tester> testers) {
        Set<String> countries = new HashSet<>();
        for (Tester tester : testers) {
            countries.add(tester.getCountry());
        }
        return countries;
    }


    /**
     * Get the qualified testers filtered by countries
     * @param testers the list of input testers
     * @param countries search criteria countries
     * @return
     */
    private List<Tester> filterTestersByCountry(List<Tester> testers, List<String> countries) {

        List<Tester> matchedTesters = new ArrayList<>();
        if (countries.contains("ALL")) {
            return testers;
        } else {
            for (String country: countries) {
                for (Tester tester : testers) {
                    if (tester.getCountry().equalsIgnoreCase(country)) {
                        matchedTesters.add(tester);
                    }
                }
            }

        }
        return matchedTesters;

    }

    /**
     * Get the qualified testers filtered by devices
     * @param testers
     * @param devices
     * @return
     */
    private List<Tester> filterTestersByDevices(List<Tester> testers, List<String> devices) {

        List<Tester> matchedTesters = new ArrayList<>();
        if (devices.contains("ALL")) {
            return testers;
        } else {
            for (Tester tester : testers) {
                for (String device : devices) {
                    if (tester.getDevices().contains(device)) {
                        //enough to keep this tester if one device is supported
                        matchedTesters.add(tester);
                        break;
                    }
                }

            }
            return matchedTesters;
        }
    }


    /**
     * Print the formatted result
     * @param countries
     * @param devices
     * @param results
     */
        private void prettyPrintResult(List<String> countries, List<String> devices, List<MatchResult> results) {
        System.out.println("Search Critiria:");
        for (String country: countries) {
            System.out.println(country);
        }
        for (String deviceName: devices) {
            System.out.println(deviceName);
        }
        System.out.println("--------------------------------------");
        System.out.println("Matched Tester Result: ");

        for(MatchResult result: results) {
            System.out.println("Tester name: " + result.getTester().getFirstName() + " " + result.getTester().getLastName());
            System.out.println("Expeirence: " + result.getTotalExpeirence());
            System.out.println("-------------------------------------------");
        }

    }




}
