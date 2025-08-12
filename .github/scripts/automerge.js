module.exports = async ({github, context}) => {
  console.log("🚀 Starting Auto Merge PR Script");
  console.log(`📊 Event: ${context.eventName}`);

  let prNumber;
  let headBranch;
  let prTitle;
  let prBody;
  let isDraft = false;
  let prSha;

  try {
    if (context.eventName === 'pull_request' && context.payload.pull_request) {
      console.log("📋 Processing pull_request event");
      const pr = context.payload.pull_request;

      prNumber = pr.number;
      headBranch = pr.head.ref;
      prTitle = pr.title;
      prBody = pr.body || "";
      isDraft = pr.draft;
      prSha = pr.head.sha;

      console.log(`📊 PR Data - Number: ${prNumber}, Branch: ${headBranch}, Draft: ${isDraft}`);
    } else if (context.eventName === 'check_suite' && context.payload.check_suite) {
      console.log("📋 Processing check_suite event");
      const checkSuite = context.payload.check_suite;
      const prs = checkSuite.pull_requests;

      if (prs && prs.length > 0) {
        const pr = prs[0];

        prNumber = pr.number;
        headBranch = pr.head.ref;
        prTitle = pr.title;
        prBody = pr.body || "";
        isDraft = pr.draft;
        prSha = checkSuite.head_sha;

        console.log(`📊 Check Suite PR Data - Number: ${prNumber}, Branch: ${headBranch}, Draft: ${isDraft}`);
      }
    }

    if (!prNumber || !headBranch) {
      console.log("❌ No pull request or branch found. Skipping auto-merge.");
      return;
    }

    if (isDraft) {
      console.log(`📝 PR #${prNumber} is in Draft mode. Skipping auto-merge.`);
      return;
    }

    if (!headBranch.startsWith("feature/")) {
      console.log(`🏷 Branch "${headBranch}" does not match "feature/*" pattern. Skipping auto-merge.`);
      return;
    }

    console.log(`🔍 Checking status for PR #${prNumber}...`);

    const {  combinedStatus } = await github.rest.repos.getCombinedStatusForRef({
      owner: context.repo.owner,
      repo: context.repo.repo,
      ref: prSha
    });

    console.log(`📊 Status check result: ${combinedStatus.state}`);

    const allChecksPassed = combinedStatus.state === "success";

    if (allChecksPassed) {
      console.log(`✅ All checks passed for PR #${prNumber}. Attempting to merge...`);

      try {
        await github.rest.pulls.merge({
          owner: context.repo.owner,
          repo: context.repo.repo,
          pull_number: prNumber,
          merge_method: "rebase",
          commit_title: prTitle,
          commit_message: prBody
        });

        console.log(`🎉 PR #${prNumber} from branch "${headBranch}" successfully merged with title "${prTitle}".`);

      } catch (mergeError) {
        console.log(`❌ Failed to merge PR #${prNumber}: ${mergeError.message}`);
      }

    } else {
      console.log(`❌ PR #${prNumber} has failing checks (${combinedStatus.state}). Skipping merge.`);
    }

  } catch (error) {
    console.log(`💥 Unexpected error in auto-merge script: ${error.message}`);
  }

  console.log("🏁 Auto Merge PR Script completed");
};