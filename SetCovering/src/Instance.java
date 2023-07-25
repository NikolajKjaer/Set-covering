import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Instance {

    ArrayList<Integer> cost = new ArrayList<Integer>();
    ArrayList<ArrayList<Integer>> rows = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> A = new ArrayList<ArrayList<Integer>>();
    int numRows;
    int numCols;


    Instance(String fileName){
        try{
            File myData = new File(fileName);
            Scanner myReader = new Scanner(myData);
            
            this.numRows = myReader.nextInt();
            this.numCols = myReader.nextInt();

            // Data on how much it costs to use each column
            for(int i=0;i<this.numCols;i++){
                this.cost.add(myReader.nextInt());
            }

            // Data on which columns cover each row
            while(myReader.hasNextInt()){
                int numCover = myReader.nextInt();
                ArrayList<Integer> tempRow = new ArrayList<Integer>();
                for(int i=0;i<numCover;i++){
                    tempRow.add(myReader.nextInt());
                }
                this.rows.add(tempRow);
            }

            // Put data on which columns cover which rows into a matrix A
            for(int i=0;i<numRows;i++){
                ArrayList<Integer> Ai = new ArrayList<Integer>();
                for(int j=0;j<numCols;j++){
                    if(rows.get(i).contains(j)){
                        Ai.add(1);
                    } else {
                        Ai.add(0);
                    }
                }
                this.A.add(Ai);
            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public int sum_array(ArrayList<Integer> array){
        int sum = 0;
        for(int num : array){
            sum += num;
        }
        return sum;
    }
}
