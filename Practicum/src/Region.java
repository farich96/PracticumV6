import java.util.Objects;

public class Region {
    public int Id;
    public String Region;
    public String Country;

    public Region(String region, String country){
        this.Region = region;
        this.Country = country;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Region.hashCode();
        result = prime * result + Country.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Region other = (Region) obj;
        if (!Objects.equals(this.Region, other.Region))
            return false;
        if (!Objects.equals(this.Country, other.Country))
            return false;
        return true;
    }
}
