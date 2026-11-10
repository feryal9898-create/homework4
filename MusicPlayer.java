
import java.util.*;

public class MusicPlayer {

    private static class Node {
        Song song;
        Node prev, next;

        Node(Song song) {
            this.song = song;
        }
    }

    private Node head, tail;

    private Map<String, Node> library = new HashMap<>();
    private Deque<Song> upNext = new ArrayDeque<>();
    private Deque<Song> history = new ArrayDeque<>();
    private Map<String, Integer> playCounts = new HashMap<>();

    private Song current;

    public boolean addSong(String id, String title, String artist) {
        if (library.containsKey(id)) return false;

        Song song = new Song(id, title, artist);
        Node node = new Node(song);

        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }

        tail = node;
        library.put(id, node);
        return true;
    }

    public Song findSong(String id) {
        Node node = library.get(id);
        return node == null ? null : node.song;
    }

    public List<String> showPlaylist() {
        List<String> titles = new ArrayList<>();

        for (Node node = head; node != null; node = node.next) {
            titles.add(node.song.title);
        }

        return titles;
    }

    private Song start(Song song) {
        current = song;
        playCounts.put(song.id,
            playCounts.getOrDefault(song.id, 0) + 1);
        return song;
    }

    public Song play(String id) {
        Song song = findSong(id);

        if (song == null) return null;

        if (current != null) {
            history.push(current);
        }

        return start(song);
    }

    public Song nowPlaying() {
        return current;
    }

    public int playCount(String id) {
        return playCounts.getOrDefault(id, 0);
    }

    public boolean queueLater(String id) {
        Song song = findSong(id);

        if (song == null) return false;

        upNext.addLast(song);
        return true;
    }

    public boolean queueNext(String id) {
        Song song = findSong(id);

        if (song == null) return false;

        upNext.addFirst(song);
        return true;
    }

    public List<String> showUpNext() {
        List<String> titles = new ArrayList<>();

        for (Song song : upNext) {
            if (library.containsKey(song.id)) {
                titles.add(song.title);
            }
        }

        return titles;
    }

    public Song next() {
        Song song = null;

        while (!upNext.isEmpty()) {
            Song candidate = upNext.removeFirst();

            if (library.containsKey(candidate.id)) {
                song = candidate;
                break;
            }
        }

        if (song == null) {
            Node node;

            if (current == null) {
                node = head;
            } else {
                node = library.get(current.id).next;
            }

            if (node == null) return null;

            song = node.song;
        }

        if (current != null) {
            history.push(current);
        }

        return start(song);
    }

    public Song back() {
        Song song = null;

        while (!history.isEmpty()) {
            Song candidate = history.pop();

            if (library.containsKey(candidate.id)) {
                song = candidate;
                break;
            }
        }

        if (song == null) return null;

        if (current != null) {
            upNext.addFirst(current);
        }

        return start(song);
    }

    public List<String> showHistory() {
        List<String> titles = new ArrayList<>();

        for (Song song : history) {
            if (library.containsKey(song.id)) {
                titles.add(song.title);
            }
        }

        return titles;
    }

    public boolean removeSong(String id) {
        Node node = library.get(id);

        if (node == null) return false;

        if (current != null && current.id.equals(id)) {
            return false;
        }

        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        library.remove(id);
        playCounts.remove(id);

        return true;
    }
}

