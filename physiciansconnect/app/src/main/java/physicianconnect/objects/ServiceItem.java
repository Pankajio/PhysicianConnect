package physicianconnect.objects;

import java.util.ArrayList;
import java.util.List;

public class ServiceItem {
    private final String name;
    private double cost;

    public ServiceItem(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public static List<ServiceItem> parseList(String str) {
        List<ServiceItem> list = new ArrayList<>();
        if (str == null || str.isEmpty())
            return list;
        for (String part : str.split(",")) {
            String[] arr = part.split(":");
            if (arr.length == 2)
                list.add(new ServiceItem(arr[0].trim(), Double.parseDouble(arr[1].trim())));
        }
        return list;
    }
}