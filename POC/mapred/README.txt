Map / Reduce proof of concepts for SLI Ingestion platform

goals:
- identify concrete steps for ingestion matching / processing over hdfs cluster, high-level validation of approach
- compare implementation complexity/constraints using several libs/toolsets (or none at all)
- performance comparison is interesting as a secondary priority but benchmarking is not a goal (yet)

steps / status:
1. hello world operation in hadoop, then also using higher-level tools

    * "word count" example
    * currently implemented with hadoop mapreduce and cascading.  note the results are slightly different but that is due to the string tokenization approach, not the M/R interface. 
    * tests don't assert anything
    
2. simple ingestion-like operation, limited data size ...
    
    * "big diff" is the operation (TODO: describe in detail)
    * currently implemented with hadoop mapreduce only.
    * test data is generated using src/main/python/bigdiff-testdata.py (TODO: describe in detail)
    * cascading implementation is unfinished.
    * tests don't assert anything

3. ingestion-like operation, on ec2, large data size ...

    * not done.

4 (tbd). add tools / libraries for comparison
5 (tbd). increase complexity of operations, increase data size ...

