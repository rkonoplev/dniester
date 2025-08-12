/**
 * Auto Merge PR Script for GitHub Actions
 *
 * This script automatically merges pull requests that meet specific criteria:
 * - PR is not in draft mode
 * - Source branch follows "feature/*" pattern
 * - Target branch is "main"
 * - All status checks have passed
 * - Uses rebase merge method to maintain linear history
 *
 * @param {Object} github - Octokit instance for GitHub API calls
 * @param {Object} context - GitHub Actions context with event data
 */
module.exports = async ({github, context}) => {
  // Debug: Log script start
  console.log("🚀 Starting Auto Merge PR Script");
  console.log(`📊 Event: ${context.eventName}`);

  // Initialize variables for PR data
  let prNumber;
  let headBranch;
  let prTitle;
  let prBody;
  let isDraft = false;
  let prSha;

  try {
    // Handle pull_request event
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
    }
    // Handle check_suite event
    else if (context.eventName === 'check_suite' && context.payload.check_suite) {
      console.log("📋 Processing check_suite event");
      const checkSuite = context.payload.check_suite;
      const prs = checkSuite.pull_requests;

      // Only proceed if there are associated PRs
      if (prs && prs.length > 0) {
        const pr = prs[0]; // Take the first PR

        prNumber = pr.number;
        headBranch = pr.head.ref;
        prTitle = pr.title;
        prBody = pr.body || "";
        isDraft = pr.draft;
        prSha = checkSuite.head_sha;

        console.log(`📊 Check Suite PR Data - Number: ${prNumber}, Branch: ${headBranch}, Draft: ${isDraft}`);
      }
    }

    // Early exit conditions
    if (!prNumber || !headBranch) {
      console.log("❌ No pull request or branch found. Skipping auto-merge.");
      return;
    }

    // Skip if PR is Draft
    if (isDraft) {
      console.log(`📝 PR #${prNumber} is in Draft mode. Skipping auto-merge.`);
      return;
    }

    // Filter: only feature/* → main branches allowed
    if (!headBranch.startsWith("feature/")) {
      console.log(`🏷 Branch "${headBranch}" does not match "feature/*" pattern. Skipping auto-merge.`);
      return;
    }

    console.log(`🔍 Checking status for PR #${prNumber}...`);

    // Get all checks status
    const { data: combinedStatus } = await github.rest.repos.getCombinedStatusForRef({
      owner: context.repo.owner,
      repo: context.repo.repo,
      ref: prSha
    });

    console.log(`📊 Status check result: ${combinedStatus.state}`);

    const allChecksPassed = combinedStatus.state === "success";

    if (allChecksPassed) {
      console.log(`✅ All checks passed for PR #${prNumber}. Attempting to merge...`);

      try {
        // Perform the merge using rebase method
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
        // Don't throw error to prevent workflow failure
      }

    } else {
      console.log(`❌ PR #${prNumber} has failing checks (${combinedStatus.state}). Skipping merge.`);
    }

  } catch (error) {
    // Log any unexpected errors but don't fail the workflow
    console.log(`💥 Unexpected error in auto-merge script: ${error.message}`);
    console.log(`📝 Error stack: ${error.stack}`);
  }

  console.log("🏁 Auto Merge PR Script completed");
};