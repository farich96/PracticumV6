import java.util.Objects;

public class ItemType {
    public int Id;
    public String TypeName;
    public ItemType(String typeName){
        this.TypeName = typeName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + TypeName.hashCode();
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
        ItemType other = (ItemType) obj;
        return Objects.equals(this.TypeName, other.TypeName);
    }
}
