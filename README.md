# CSCI-251 Concepts of Parallel and Distributed Systems (CoPaDS)

For more info, please visit [my teaching page](https://www.cs.rit.edu/~ph/teaching).

For RIT students, you can find more information on myCourses.

Run ```mvn package``` inside the folder of each module to compile and generate a .jar file.


If you need to sync your forked repo with upstream repo, check [here](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/syncing-a-fork)

# Sync up to this repo

Add `upstream` repo URL to your forked repo
```bash
git remote add upstream https://gitlab.com/SpiRITlab/CoPaDS.git
```

Then if you type ```git remote -v``` if should show the following
```bash
origin	https://gitlab.com/YOUR-USERNAME/CoPaDS.git (fetch)
origin	https://gitlab.com/YOUR-USERNAME/CoPaDS.git (push)
upstream	https://gitlab.com/SpiRITlab/CoPaDS.git (fetch)
upsream		https://gitlab.com/SpiRITlab/CoPaDS.git (push)
```

Now, run this command to pull from this repo
```bash
git pull upstream master
```

Once, the pull command is done. Your repo should have my latest changes. Then push the changes to your forked repo.
```bash
git push
```

Note, you might need to resolve conflicts if there are mismatches in the two repo.


# Docker container
Build image with
```
docker build -t csci251:latest .
```

Verify the new image with 
```
docker images
```

Start a container
```
docker run -it csci251 /bin/bash
```


# Docker-compose
Build and start docker containers
```
docker-compose up
```
use `--build` to rebuild docker image

Attach to the running docker containers
```
docker exec -it <CONTAINER-NAME> bash
```
where `<CONTAINER-NAME>` should be replaced with your targeted container name.