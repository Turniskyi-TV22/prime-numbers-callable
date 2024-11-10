package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);
        int limit = 1000;
        System.out.print("Enter number:");
        int number = scanner.nextInt();


        //version without callable for compare
        long startTime = System.currentTimeMillis();
        CopyOnWriteArrayList<Integer> primes = findPrimes(0, number);
        for(Integer prime : primes)
        {
            System.out.print(prime + " ");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("\nTime taken with one thread: " + (endTime - startTime) + " ms");


        //version with callable
        System.out.println();
        startTime = System.currentTimeMillis();
        int index = 0;
        List<Callable<CopyOnWriteArrayList<Integer>>> tasks = new ArrayList<Callable<CopyOnWriteArrayList<Integer>>>();
        do{
            int finalIndex = index;
            if(index+limit < number) {
                Callable<CopyOnWriteArrayList<Integer>> task = () -> findPrimes(finalIndex, finalIndex + limit);
                tasks.add(task);
                index += limit;
            }
            else
            {
                Callable<CopyOnWriteArrayList<Integer>> task = () -> findPrimes(finalIndex, number);
                tasks.add(task);
                index = number;
            }
        } while(index < number);



        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<CopyOnWriteArrayList<Integer>>> results = executorService.invokeAll(tasks);

        while (true) {
            boolean allDone = true;
            for (Future<CopyOnWriteArrayList<Integer>> future : results) {
                if (!future.isDone()) {
                    allDone = false;
                    break;
                }
            }

            if (allDone) {
                break;
            }
        }
        primes = new CopyOnWriteArrayList<>();
        for (Future<CopyOnWriteArrayList<Integer>> future : results) {
            primes.addAll(future.get()); // Извлекаем и добавляем все простые числа из каждой задачи
        }
        System.out.println("Prime numbers:");
        for (Integer prime : primes) {
            System.out.print(prime + " ");
        }

        endTime = System.currentTimeMillis();
        System.out.println("\nTime taken with multi-threads: " + (endTime - startTime) + " ms");
        executorService.shutdown();

        scanner.close();
    }
    public static CopyOnWriteArrayList<Integer> findPrimes(int startN, int endN) {
        CopyOnWriteArrayList<Integer> primes = new CopyOnWriteArrayList<>();
        for(int i = startN; i < endN; i++)
        {
            boolean isPrime = true;
            for(int j = 2; j < i; j++)
            {
                if (i % j == 0)
                {
                    isPrime = false;
                    continue;
                }
            }
            if(isPrime) primes.add(i);
        }
        return primes;
    }
}