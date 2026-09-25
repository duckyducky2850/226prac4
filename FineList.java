public class FineList 
{

    private final Node head;
    private final Node tail;

    public FineList() 
    {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);

        head.next = tail;
    }

    public boolean add(int value) 
    {
        // Use hand-over-hand locking.

        head.lock.lock();
        Node pred = head;
        
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                //hand-over-hand
                //dont release pred until curr is locked
                while (curr.value < value) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock(); //lock before moving on
                }
                if (curr.value == value) {
                    return false; //already there
                }

                Node newNode = new Node(value);
                newNode.next = curr;
                pred.next = newNode;
                return true;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean remove(int value) 
    {
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.value < value) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }

                if (curr.value == value) {
                    pred.next = curr.next;
                    return true;
                }
                return false;

            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean contains(int value) 
    {
        head.lock.lock();
        Node pred = head;

        try{
            Node curr = pred.next;
            curr.lock.lock();

            try{
            while(curr.value < value){
                pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
            }
            return curr.value==value;
            }
            finally{
                curr.lock.unlock();
            }
        }
        finally{
            pred.lock.unlock();
        }
        
    }
}