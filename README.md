# MerchMob

A marketplace Android app built with Java and Realm.

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable version)
- Git installed on your machine ([download here](https://git-scm.com/downloads))
- A GitHub account with access to this repository

### Cloning the Repository

**Option A: Clone via terminal (SSH)**

```bash
git clone git@github.com:calilaceron/MerchMob.git
cd MerchMob
```

> Requires an SSH key added to your GitHub account. Check with `ssh -T git@github.com` — you should see a success message.

**Option B: Clone via terminal (HTTPS)**

```bash
git clone https://github.com/calilaceron/MerchMob.git
cd MerchMob
```

**Option C: Clone directly from Android Studio**

1. Open Android Studio
2. On the Welcome screen, click **Get from VCS** (or `File → New → Project from Version Control` if a project is already open)
3. Select **Git**, paste in the repository URL (`https://github.com/calilaceron/MerchMob.git` or the SSH equivalent)
4. Choose a local directory and click **Clone**

### Opening the Project

Once cloned, open the project folder in Android Studio and let Gradle sync. This may take a few minutes on first open as dependencies (including Realm) are downloaded.

---

## Git & GitHub Workflow in Android Studio

### Signing in to GitHub

`File → Settings → Version Control → GitHub` → click **+** → log in via browser OAuth.

This enables push/pull, PR creation, and cloning from inside the IDE.

### Common Actions

The branch popup (click the branch name in the bottom-right corner of the IDE, or `Git → Branches`) is your main hub:

| Action                | Shortcut (Win/Linux) | Shortcut (Mac) |
|-----------------------|----------------------|----------------|
| Update Project (pull) | `Ctrl+T`             | `Cmd+T`        |
| Commit                | `Ctrl+K`             | `Cmd+K`        |
| Push                  | `Ctrl+Shift+K`       | `Cmd+Shift+K`  |
| New Branch            | `Ctrl+Alt+N`         | `Cmd+Alt+N`    |
| Git history / log     | `Alt+9`              | `Cmd+9`        |

### Feature Branch Naming Convention

All feature branches are created off `master` and follow this format:

```
[yourname]/feature-name
```

**Examples:**
- `calil/product-listing-fix`
- `calil/photo-upload`
- `calil/realm-schema-update`

This keeps it clear who's working on what and avoids branch name collisions.

### Working on a Feature — Step by Step

1. **Make sure you're on `master` and up to date**
   - Click the branch popup → select `master` under **Local** → checkout
   - Click **Update Project** (`Ctrl+T`) to pull the latest changes

2. **Create your feature branch**
   - Click the branch popup → **New Branch...** (`Ctrl+Alt+N`)
   - Name it `[yourname]/feature-name`
   - Android Studio automatically checks out the new branch for you

3. **Make your changes** in the code

4. **Commit your work**
   - `Ctrl+K` opens the commit dialog
   - Review the changed files, write a clear commit message, click **Commit**
   - (Or **Commit and Push** to do both in one step)

5. **Push your branch**
   - `Ctrl+Shift+K` → confirm the push
   - First push on a new branch will prompt you to set the remote tracking branch — accept the default

6. **Open a Pull Request into `master`**
   - `Git → GitHub → Create Pull Request`
   - Confirm the base branch is `master` and the compare branch is your feature branch
   - Add a title/description and submit

7. **After the PR is merged**
   - Switch back to `master` in the branch popup
   - Click **Update Project** (`Ctrl+T`) to pull the merged changes
   - You can delete your local feature branch: branch popup → right-click the branch → **Delete**

### Resolving Merge Conflicts

If a pull or merge results in conflicts, Android Studio opens a three-way merge tool showing your version, the base version, and the incoming changes. Resolve conflicts line-by-line, then mark files as resolved and complete the merge.

---

## Tech Stack

- **Language:** Java
- **Database:** Realm
- **IDE:** Android Studio

## Notes

- Minimum supported Android version and other build details are in `app/build.gradle`.
- If storage permission issues occur on Android 14+, ensure the app is using the updated media/storage permission model rather than legacy `WRITE_EXTERNAL_STORAGE`.
- When updating master, make sure to sync the Gradle setup to reflect the Realm configurations.