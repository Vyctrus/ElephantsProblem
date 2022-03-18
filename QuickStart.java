import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

class QuickStart {
    Scanner stdInput;
    int howMany;
    float[] elekSizes;
    int[] elekA;
    int[] elekB;

    int[] nextTab;
    boolean[] considered;
    List<List<Integer>> cycles;

    float[] weightOfElek;

    public void readDataTest() {
        // just for testing
        printInputData(howMany, elekSizes, elekA, elekB);
    }

    public void prepareGraph() {
        nextTab = new int[howMany];
        considered = new boolean[howMany];
        for (int i = 0; i < howMany; i++) {
            nextTab[elekB[i]] = elekA[i]; // slon o numerze x, ma zjac miejsce tego slonia co ma index y
            considered[i] = false;
        }
    }

    public void separeteCycles() {
        cycles = new ArrayList<List<Integer>>();
        for (int i = 0; i < howMany; i++) {
            if (!considered[i]) {
                considered[i] = true;
                int startPoint = i;
                List<Integer> singleCycle = new ArrayList<>();
                singleCycle.add(startPoint);
                int currentPoint = nextTab[startPoint];
                while (true) {
                    if (startPoint == currentPoint) {
                        // endOfCycle
                        cycles.add(singleCycle);
                        break;
                    } else {
                        singleCycle.add(currentPoint);
                        considered[currentPoint] = true;
                        currentPoint = nextTab[currentPoint];
                    }
                }
            }
        }
    }

    public void separeteCyclesTest() {
        // test: eqpected cycles printed in separete lines
        for (var item : cycles) {
            for (var elephant : item) {
                System.out.print(" " + elephant);
            }
            System.out.print("\n");
        }
    }

    public void addWeightToElekNumber() {
        // Masa słonia o numerze NIE indexie!
        weightOfElek = new float[howMany];
        for (int i = 0; i < weightOfElek.length; i++) {
            weightOfElek[elekA[i]] = elekSizes[i];
        }
    }

    public void addWeightToElekNumberTest() {
        // testing, print all sizes in numeric way
        System.out.println("Weights in numeric way");
        for (var item : weightOfElek) {
            System.out.print(item + " ");
        }
        System.out.print("\n");
    }

    public int numberOfSmallElek() {
        float minWeight = elekSizes[0];
        int minWeightIndex = 0;
        for (int i = 0; i < elekSizes.length; i++) {
            if (elekSizes[i] < minWeight) {
                minWeight = elekSizes[i];// what if two eleks have same size?
                minWeightIndex = i;
            }
        }
        return elekA[minWeightIndex];
    }

    // return number of the smallest elek in the list/cycle
    public int getMinC(List<Integer> copiedCycle) {
        float minC = weightOfElek[copiedCycle.get(0)];
        int minCnumber = copiedCycle.get(0);
        for (int j = 0; j < copiedCycle.size(); j++) {
            if (weightOfElek[copiedCycle.get(j)] < minC) {
                minC = weightOfElek[copiedCycle.get(j)];
                minCnumber = copiedCycle.get(j);
            }
        }
        return minCnumber;
    }

    public int getPreviousOf(List<Integer> copiedCycle, int someVerticle) {
        int previous = 0;
        for (int j = 0; j < copiedCycle.size(); j++) {
            if (nextTab[j] == someVerticle) {
                previous = j;
            }
        }
        return previous;
    }

    public float method1(int cycleId) {
        float method1Cost = 0;
        List<Integer> copiedCycle = new ArrayList<>(cycles.get(cycleId));
        int minCnumber = getMinC(copiedCycle);
        // Method 1 for cycle copiedCycle
        while (nextTab[minCnumber] != minCnumber) {
            int previous = getPreviousOf(copiedCycle, minCnumber);
            // step1/3
            nextTab[previous] = nextTab[minCnumber];
            // 2/3
            nextTab[getPreviousOf(copiedCycle, previous)] = minCnumber;
            // 3/3
            nextTab[minCnumber] = previous;

            method1Cost += weightOfElek[minCnumber];
            method1Cost += weightOfElek[previous];
        }
        return method1Cost;
    }

    public float method2(int cycleId, int minAbs) {
        float method2Cost = 0;
        List<Integer> copiedCycle = new ArrayList<>(cycles.get(cycleId));
        int minCnumber = getMinC(copiedCycle);

        if (weightOfElek[minCnumber] <= weightOfElek[minAbs]) {
            minAbs = minCnumber;
            return method1(cycleId);
        } else {
            // save data before swap
            // int savePreviousMinAbs=getPreviousOf(copiedCycle,minAbs); //no need
            int saveNextMinAbs = nextTab[minAbs];
            // int saveMinCNumber = minCnumber;

            // swap minC with minAbs
            method2Cost += weightOfElek[minCnumber];
            method2Cost += weightOfElek[minAbs];
            int previous = getPreviousOf(copiedCycle, minCnumber);
            nextTab[previous] = minAbs;
            nextTab[minAbs] = nextTab[minCnumber];

            while (nextTab[minAbs] != minAbs) {
                previous = getPreviousOf(copiedCycle, minCnumber);
                // step1/3
                nextTab[previous] = nextTab[minCnumber];
                // 2/3
                nextTab[getPreviousOf(copiedCycle, previous)] = minCnumber;
                // 3/3
                nextTab[minCnumber] = previous;
                method2Cost += weightOfElek[minCnumber];
                method2Cost += weightOfElek[previous];
            }

            // restore swap
            method2Cost += weightOfElek[minCnumber];
            method2Cost += weightOfElek[minAbs];
            nextTab[minAbs] = saveNextMinAbs;
            return method2Cost;
        }
    }

    public static void main(String[] args) {
        QuickStart qs = new QuickStart();
        qs.mainProgram();
    }

    public void mainProgram() {

        readData();
        readDataTest();
        // Loaded data, evrything good
        // Graph preparation section
        prepareGraph();
        // Cycles separation
        separeteCycles();
        separeteCyclesTest();
        addWeightToElekNumber();
        addWeightToElekNumberTest();
        // find minAbs
        int minAbsIndex = numberOfSmallElek();// number elek minAbs.
        // from specified cycle
        // for each specified cycle
        float totalCost = 0;
        for (int i = 0; i < cycles.size(); i++) {
            float method1Cost = method1(i);
            float method2Cost = method2(i, minAbsIndex);

            if (method1Cost <= method2Cost) {
                totalCost += method1Cost;
            } else {
                totalCost += method2Cost;
            }
            // and thats..all? except bugs?
        }
        System.out.println("Ostateczny wynik to pamparampamp!!!: " + totalCost);
    }

    public static int[] loadIntArray(int size, Scanner stdInput) {
        String stdLine = stdInput.nextLine();
        StringTokenizer st = new StringTokenizer(stdLine, " ");
        int[] resultArray = new int[size];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = Integer.parseInt(st.nextToken()) - 1;// java indexing starts at 0, easier to maintain
        }
        return resultArray;
    }

    public static float[] loadSizeArray(int size, Scanner stdInput) {
        String stdLine = stdInput.nextLine();
        StringTokenizer st = new StringTokenizer(stdLine, " ");
        float[] resultArray = new float[size];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = Float.parseFloat(st.nextToken());
        }
        return resultArray;
    }

    public static void printInputData(int howMany, float[] elekSizes, int[] elekA, int[] elekB) {
        System.out.println("How many: " + howMany);
        for (float number : elekSizes) {
            System.out.print(number + " ");
        }
        System.out.print("\n");
        for (int number : elekA) {
            System.out.print(number + " ");
        }
        System.out.print("\n");
        for (int number : elekB) {
            System.out.print(number + " ");
        }
        System.out.print("\n");
    }

    public void readData() {
        stdInput = new Scanner(System.in); // Create a Scanner object
        System.out.println("How many?");
        howMany = Integer.parseInt(stdInput.nextLine()); // Read user input

        System.out.println("Elek sizes");
        elekSizes = loadSizeArray(howMany, stdInput);

        System.out.println("Elek positions");
        elekA = loadIntArray(howMany, stdInput);

        System.out.println("Elek desired positions");
        elekB = loadIntArray(howMany, stdInput);

        System.out.println("Loading data comlpeted");
    }
}