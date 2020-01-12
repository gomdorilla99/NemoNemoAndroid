package com.example.nemonemo;

// Single linked list
public class LinkList<T>
{
    protected Node<T> mpRootNode;
    
    public  int GetCount(Node<T> base_node)
    {
        Node<T> temp_node;
        int count = 0;
        temp_node = base_node;
        if (base_node==null)
            return 0;
        while (true)
        {
            count++;
            if (temp_node.pNext == null)
                break;
            temp_node = temp_node.pNext;
        }
        return count;
    }

    @Override
    public void finalize(){ DeleteAllNode(); }

    public LinkList(){ mpRootNode = null; }
    public  Node<T> GetHeaderNode() { return mpRootNode; };
    
    public  Node<T> AddToTail(Node<T> newNode)
    {
        Node<T> pTail = GetTail(mpRootNode);
        if (pTail == null && mpRootNode == null)
        {
            mpRootNode = newNode;
            return newNode;
        }
        else
        {
            pTail.pNext = newNode;
            newNode.pNext = null;
            return newNode;
        }
    }
    
    public  Node<T> GetItem(int index)
    {
        Node<T> temp_node;
        int count = 0;
        temp_node = mpRootNode;
        while (true)
        {
            if (temp_node.pNext == null || index == count)
                break;
            count++;
            temp_node = temp_node.pNext;
        }
        return temp_node;
    }
    
    public  Node<T> GetTail(Node<T> pCur_Node)
    {

        while (pCur_Node != null && pCur_Node.pNext != null)
            pCur_Node = pCur_Node.pNext;
        return pCur_Node;
    }
    
    public int GetCount() { return GetCount(mpRootNode); }
    
    public int DeleteAllNode()
    {
        DeleteNode(mpRootNode);
        mpRootNode = null;
        return 0;

    }
    
    public int DeleteNode(Node<T> base_node)
    {
        Node<T> temp_node;
        Node<T> next_node;

        temp_node = base_node;
        next_node = temp_node;
        while (true)
        {
            if (next_node != null)
            {
                next_node = temp_node.pNext;
                if (mpRootNode == temp_node)
                {
                    mpRootNode = next_node;
                }
            }
            else
            {
                if (mpRootNode == temp_node)
                {
                    mpRootNode = null;
                }
            }
            if (next_node == null)
                break;
            temp_node = next_node;
        }
        return 0;
    }

    /*
    public int InsertNode(Node<T> base_node, Node<T> in_node)
    {

        Node<T> temp_node;
        int count = 0;

        if (base_node == null)
        {
            if (mpRootNode != null)
            {
                base_node = mpRootNode;
            }
            else
            {
                mpRootNode = in_node;
                return 0;
            }
        }
        temp_node = base_node;
        while (true)
        {
            if (temp_node.pNext == null)
                break;
            count++;
            temp_node = temp_node.pNext;
        }
        temp_node.pNext = in_node;
        return 1;
    }
    */
};
