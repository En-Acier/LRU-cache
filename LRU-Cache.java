import java.util.HashMap;
class Entry {
    int value;
    int key;
    Entry left;
    Entry right;
}

class LRUCache {
    HashMap<Integer, Entry> hashmap;
    Entry start, end;
    int capacity;

    public LRUCache(int capacity) {
        hashmap = new HashMap<Integer, Entry>();
        this.capacity = capacity;
    }
    
    public int get(int key) {
        if (hashmap.containsKey(key)){
            Entry entry = hashmap.get(key);
            removeEntry(entry);
            replaceAtFront(entry);
            return entry.value;
        }
        return -1;
    }
    
    public void put(int key, int value) {
        if (hashmap.containsKey(key)){
            Entry entry = hashmap.get(key);
            entry.value = value;
            removeEntry(entry);
            replaceAtFront(entry);
        }
        else {
            Entry newEntry = new Entry();
            newEntry.left = null;
            newEntry.right = null;
            newEntry.value = value;
            newEntry.key = key;
            if (hashmap.size() > capacity){
                hashmap.remove(end.key);
                removeEntry(end);
                replaceAtFront(newEntry);
            }
            else{
                replaceAtFront(newEntry);
            }
            hashmap.put(key, newEntry);
        }
    }

    public void replaceAtFront(Entry entryToAdd){
        entryToAdd.right = start;
        entryToAdd.left = null;
        if (start != null){
            start.left = entryToAdd;
        }
        start = entryToAdd;
        if (end == null){
            end = start;
        }
    }

    public void removeEntry(Entry entryToRemove){
        if (entryToRemove.left != null) {
            entryToRemove.left.right = entryToRemove.right;
        }
        else{
            start = entryToRemove.right;
        }
        if (entryToRemove.right != null) {
            entryToRemove.right.left = entryToRemove.left;
        }
        else{
            end = entryToRemove.left;
        }
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */