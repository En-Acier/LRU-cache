# LRU-cache
An O(1) LRU Cache implementation in Java

# What's an LRU Cache?

An LRU (Last Recently Used) cache is a method of storing data in a cache of a fixed size. Where once we try to add data the cache is full, the last recently used item in the cache is dropped to make space.

Items in a cache data structure consist of a *value* i.e., the item being stored in the cache and the *key*, an identifer for where an item is stored in the cache.

These caches also utilise a *get* function, which retrieves the *value* for a given *key*, and a *put* function, which adds a new value to the cache.

For example, if our cache of...
>1, 2, 3, 4, 5

is at capacity, adding the value *6* would give this result...

>6, 1, 2, 3, 4

Because of this, an LRU cache requires not only that we keep track of the data in the cache, but also the **order** of that data.

# How to think about it

There are many ways to keep track of the last accessed item of data. We *could* potentially implement one of the two following solutions...
1. Implement timestamps to keep track of when each item of data was last accessed - but storing timestamps means increasing the metadata. That isn't ideal for small datastores like a cache.

So what if instead of keeping track of the exact time each element was accessed, we **abstract** our thinking, and only keep track of each element's access time **relative** to one another?

2. Store the data in a **sorted** data structure, like a linked list.

In a linked list, the data would look like this...
> 1 --> 2 --> 3 --> 4

and running *get(2)* would alter the list to look like this...

> 2 --> 1 --> 3 --> 4

Now if we add to the list, we would get...

> 5 --> 2 --> 1 --> 3

This doesn't add metadata, but does create new overhead. Every time an element is retrieved, we must iterate over the list to find the correct item. So *get* and *put* would run at O(n) complexity.

## How do we avoid iterating through the entire list?

Once again, we *could* add some sort of lookup table (like a set) that informs us if an item is in the array before retrieving it, cutting down on search time. But the list still needs to be iterated through to retrieve an element if it exists in the list.

How about instead, we had a sort of lookup table that not only told us if an item existed, but also provided a pointer to where each element is?

# The solution

This solution makes use of a ***doubly linked list*** , where each entry in the cache points to the entry on it's left and it's right. Storing data this way changes how the *get* and *put* methods would otherwise work.

```
class Entry {
    int value;
    int key;
    Entry left;
    Entry right;
}
```

As you can see, each entry class keeps track of the entry to it's left and each entry to it's right.

## Creating a hashmap
```
class LRUCache {
    HashMap<Integer, Entry> hashmap;
    Entry start, end;
    int capacity;

    public LRUCache(int capacity) {
        hashmap = new HashMap<Integer, Entry>();
        this.capacity = capacity;
    }
```
The LRU Cache is created using a hashmap of each item of data to be stored, and of it's pointers.

## Getting an element

The get function here makes use of the pointer hashmap to first check if an item exists in the list before searching for it with the *.containsKey* function.
```
public int get(int key) {
        if (hashmap.containsKey(key)){
```
Only if the key exists in the hashmap does the program look for the element.
`Entry entry = hashmap.get(key);`
Now that the entry in question has been accessed, it's position in the cache must be updated. Because programming best practices state that each function should only perform one function, functions for removing the entry and replacing it at the front of the cache are called.
```
removeEntry(entry);
            replaceAtFront(entry);
            return entry.value;
        }
```
The function exits if the entry it's trying to get doesn't exist
`return -1; }`

## Putting an element

If the entry already exists in the cache, it's position is updated.
```
 public void put(int key, int value) {
        if (hashmap.containsKey(key)){
            Entry entry = hashmap.get(key);
            entry.value = value;
            removeEntry(entry);
            replaceAtFront(entry);
        }
```
Otherwise, a new entry is created.
```
else {
            Entry newEntry = new Entry();
            newEntry.left = null;
            newEntry.right = null;
            newEntry.value = value;
            newEntry.key = key;
```
If the cache is at capacity, the last recently used entry is discarded before the new entry is added at the front.
```
if (hashmap.size() > capacity){
                hashmap.remove(end.key);
                removeEntry(end);
                replaceAtFront(newEntry);
            }
```
If there is free space, the LRU entry does not need to be discarded
```
else{
                replaceAtFront(newEntry);
            }
```
After a new entry is created and space is made, a recursive call allows the new entry to be placed into the list (the recursive call will fall into the first if block)
`hashmap.put(key, newEntry);`

## New functions

These functions are unique to this implementation. Whilst adding new functions does increase the complexity of the solution and the filesize of the script, the comparisons the script is making at runtime and the amount of data in the cache is actually reduced with these additions.

### Adding a new entry to the front of the cache

When adding a new entry to the cache, the entry to it's right will be the previous start of the list. Because it is at the leftmost position, no entry is to it's left.
```
public void replaceAtFront(Entry entryToAdd){
        entryToAdd.right = start;
        entryToAdd.left = null;
```
When an entry is added to the list, it's left pointer is empty. So the previously leftmost entry must have it's pointer updated with the entry we're adding now.
```
if (start != null){
            start.left = entryToAdd;
        }
```
We make note that this newly added entry is the new start of the cache (the leftmost entry).
`start = entryToAdd;`
If the list is empty, the newly added item is also the end of the cache.
```
if (end == null){
            end = start;
        }
```

### Removing an entry

If the item to be removed is **not** the leftmost entry, the pointer of the item to it's left is updated to point at the entry to it's right.

E.g., for the list ` 1 -- > 2 -- > 3 ` to remove the number *2*, the pointer pointing to 2 will need to instead point to 3.
```
public void removeEntry(Entry entryToRemove){
        if (entryToRemove.left != null) {
            entryToRemove.left.right = entryToRemove.right;
        }
```
If the entry to remove *is* the start, then the entry to it's right is the new start.
```
else{
            start = entryToRemove.right;
        }
```
And vice versa for the right side pointers, and for the item at the end of the cache.
```
if (entryToRemove.right != null) {
            entryToRemove.right.left = entryToRemove.left;
        }
        else{
            end = entryToRemove.left;
        }
```
